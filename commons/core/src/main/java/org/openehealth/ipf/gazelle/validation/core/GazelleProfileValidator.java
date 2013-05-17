/*
 * Copyright 2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.openehealth.ipf.gazelle.validation.core;


import java.util.ArrayList;
import java.util.List;

import ca.uhn.hl7v2.*;
import ca.uhn.hl7v2.model.primitive.TSComponentOne;
import org.apache.commons.collections.CollectionUtils;
import org.openehealth.ipf.gazelle.validation.core.stub.HL7V2XConformanceProfile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.uhn.hl7v2.model.Composite;
import ca.uhn.hl7v2.model.DataTypeException;
import ca.uhn.hl7v2.model.Group;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.model.Primitive;
import ca.uhn.hl7v2.model.Segment;
import ca.uhn.hl7v2.model.Structure;
import ca.uhn.hl7v2.model.Type;
import ca.uhn.hl7v2.parser.EncodingCharacters;
import ca.uhn.hl7v2.parser.PipeParser;
import ca.uhn.hl7v2.util.Terser;

import org.openehealth.ipf.gazelle.validation.core.stub.HL7V2XStaticDef;
import org.openehealth.ipf.gazelle.validation.core.stub.SegmentType;

import static org.openehealth.ipf.gazelle.validation.core.util.MessageUtils.*;
import static org.openehealth.ipf.gazelle.validation.core.util.ProfileAssertions.profileNotFollowedAssert;
import static org.openehealth.ipf.gazelle.validation.core.util.ProfileAssertions.profileNotHL7Compliant;
import static org.openehealth.ipf.gazelle.validation.core.util.ProfileValidationMessage.*;

/**
 * A slightly modified conformance profile validator from HAPI
 * to comply the Gazelle GUI Client validation results..
 *
 *
 */
public class GazelleProfileValidator extends HapiContextSupport implements ProfileValidator<HL7V2XConformanceProfile> {

    private EncodingCharacters enc;
    private static final Logger log = LoggerFactory.getLogger(GazelleProfileValidator.class);
    private boolean validateChildren = true;

    public GazelleProfileValidator() {
        this(new DefaultHapiContext());
    }

    public GazelleProfileValidator(HapiContext context) {
        super(context);
        enc = new EncodingCharacters('|', null); // the | is assumed later -- don't change
    }

    /**
     * If set to false (default is true), each testXX and validateXX method will only test the
     * direct object it is responsible for, not its children.
     */
    public void setValidateChildren(boolean validateChildren) {
        this.validateChildren = validateChildren;
    }

    @Override
    public HL7Exception[] validate(Message message, HL7V2XConformanceProfile profile) {
        List<HL7Exception> violations = new ArrayList<HL7Exception>();
        Terser terser = new Terser(message);

        HL7V2XStaticDef staticDef = null;
        for (Object ref : profile.getDynamicDevesAndHL7V2XStaticDevesAndHL7V2XStaticDefReves()){
            if (ref.getClass().isAssignableFrom(HL7V2XStaticDef.class)){
                staticDef = (HL7V2XStaticDef)ref;
            }
        }

        checkMSHTypeField(staticDef.getMsgType(), terser, violations);
        checkMSHEventField(staticDef.getEventType(), terser, violations);
        checkMSHStructureField(staticDef.getMsgStructID(), terser, violations);
        checkMSHVersionField(profile.getHL7Version(), terser, violations);

        violations.addAll(testGroup(message, staticDef.getSegmentsAndSegGroups()));
        return violations.toArray(new HL7Exception[violations.size()]);
    }


    private List<HL7Exception> testGroup(Group group, List<Object> profile){
            //throws ProfileException {
        List<HL7Exception> exList = new ArrayList<HL7Exception>();
        List<String> allowedStructures = new ArrayList<String>();
        for (Object struct : profile) {
            String name = null;
            String usage = "";
            int max = 0;
            int min = 0;
            if (struct.getClass().isAssignableFrom(SegmentType.class)){
                SegmentType structure = ((SegmentType)struct);
                usage = structure.getUsage();
                name = structure.getName();
                if (structure.getMax().equals("*")){
                    max = Integer.MAX_VALUE;
                } else {
                    max = Integer.valueOf(structure.getMax());
                }
                min = structure.getMin().intValue();
            } else if (struct.getClass().isAssignableFrom(HL7V2XStaticDef.SegGroup.class)){
                HL7V2XStaticDef.SegGroup segGroup = ((HL7V2XStaticDef.SegGroup)struct);
                usage = segGroup.getUsage();
                name = segGroup.getName();
                if (segGroup.getMax().equals("*")){
                    max = Integer.MAX_VALUE;
                } else {
                    max = Integer.valueOf(segGroup.getMax());
                }
                min = segGroup.getMin().intValue();
            }

            if (!usage.equalsIgnoreCase("X")){
                CollectionUtils.addIgnoreNull(allowedStructures, name);

                try {
                    List<Structure> instancesWithContent = new ArrayList<Structure>();
                    for (Structure instance : group.getAll(name)) {
                        if (!instance.isEmpty())
                            instancesWithContent.add(instance);
                    }
                    exList.addAll(testCardinality(instancesWithContent.size(), min, max, usage, name));

                    // test children on instances with content
                    if (validateChildren) {
                        for (Structure s : instancesWithContent) {
                            List<HL7Exception> childExceptions = testStructure(s, struct);
                            exList.addAll(childExceptions);
                        }
                    }

                } catch (HL7Exception he) {
                    exList.add(profileNotHL7Compliant(PROFILE_STRUCTURE_NOT_EXIST_IN_JAVA_CLASS, name));
                }
            }
        }

        // complain about X structures that have content
        exList.addAll(checkForExtraStructures(group, allowedStructures));
        return exList;
    }


    /**
     * Checks a group's children against a list of allowed structures for the group (ie those
     * mentioned in the profile with usage other than X). Returns a list of exceptions representing
     * structures that appear in the message but are not supposed to.
     */
    private List<HL7Exception> checkForExtraStructures(Group group, List<String> allowedStructures) {
        List<HL7Exception> exList = new ArrayList<HL7Exception>();
        for (String childName : group.getNames()) {
            if (!allowedStructures.contains(childName)) {
                try {
                    for (Structure rep : group.getAll(childName)) {
                        CollectionUtils.addIgnoreNull(exList,
                            profileNotFollowedAssert(!rep.isEmpty(), STRUCTURE_NOT_DEFINED_IN_PROFILE, childName));
                    }
                } catch (HL7Exception he) {
                    exList.add(new HL7Exception("Problem checking profile"));
                }
            }
        }
        return exList;
    }

    /**
     * Checks cardinality and creates an appropriate exception if out of bounds. The usage code is
     * needed because if min cardinality is > 0, the min # of reps is only required if the usage
     * code is 'R' (see HL7 v2.5 section 2.12.6.4).
     *
     * @param reps the number of reps
     * @param min the minimum number of reps
     * @param max the maximum number of reps (-1 means *)
     * @param usage the usage code
     * @param name the name of the repeating structure (used in exception msg)
     * @return null if cardinality OK, exception otherwise
     */
    protected List<HL7Exception> testCardinality(int reps, int min, int max, String usage, String name) {
        List<HL7Exception> violations = new ArrayList<HL7Exception>();
        CollectionUtils.addIgnoreNull(violations, profileNotFollowedAssert(reps < min && usage.equalsIgnoreCase("R"),
                LESS_THAN_MINIMUM_CARDINALITY, name, min, reps));

        CollectionUtils.addIgnoreNull(violations, profileNotFollowedAssert(max > 0 && reps > max,
                MORE_THAN_MAXIMUM_CARDINALITY, name, max, reps));

        CollectionUtils.addIgnoreNull(violations, profileNotFollowedAssert(reps > 0 && usage.equalsIgnoreCase("X"),
                NOT_SUPPORTED_ELEMENT_PRESENT, name));

        return violations;
    }


    /**
     * Tests a structure (segment or group) against the corresponding part of a profile.
     */
    public List<HL7Exception> testStructure(Structure s, Object profile){
        List<HL7Exception> exList = new ArrayList<HL7Exception>();
        if (profile instanceof SegmentType) {
            if (Segment.class.isAssignableFrom(s.getClass())) {
                exList.addAll(testSegment((Segment) s, (SegmentType) profile));
            } else {
                exList.add(profileNotHL7Compliant(PROFILE_STRUCTURE_MISSMATCH,
                        "segment", s.getClass().getName()));
            }
        } else if (profile instanceof HL7V2XStaticDef.SegGroup) {
            if (Group.class.isAssignableFrom(s.getClass())) {
                exList.addAll(testGroup((Group) s, ((HL7V2XStaticDef.SegGroup) profile).getSegGroupsAndSegments()));
            } else {
                exList.add(profileNotHL7Compliant(PROFILE_STRUCTURE_MISSMATCH,
                        "group", s.getClass().getName()));            }
        }
        return exList;
    }


    public List<HL7Exception> testSegment(Segment segment, SegmentType profile) {
        List<HL7Exception> exList = new ArrayList<HL7Exception>();
        List<Integer> allowedFields = new ArrayList<Integer>();
        int i = 1;
        for (SegmentType.Field field : profile.getFields()) {

            // only test a field in detail if it isn't X
            if (!field.getUsage().equalsIgnoreCase("X")) {
                allowedFields.add(i);

                // see which instances have content
                try {
                    Type[] instances = segment.getField(i);
                    List<Type> instancesWithContent = new ArrayList<Type>();
                    for (Type instance : instances) {
                        if (!instance.isEmpty())
                            instancesWithContent.add(instance);
                    }

                    int max;
                    if (field.getMax().equals("*")){
                        max = Integer.MAX_VALUE;
                    } else {
                        max = Integer.valueOf(field.getMax());
                    }

                    exList.addAll(testCardinality(instancesWithContent.size(), field.getMin().intValue(),
                                  max, field.getUsage(), field.getName()));

                    for (HL7Exception exc: exList){
                        exc.setFieldPosition(i);
                    }

                    // test field instances with content
                    if (validateChildren) {
                        for (Type s : instancesWithContent) {
                            boolean escape = true; // escape field value when checking length
                            if (profile.getName().equalsIgnoreCase("MSH") && i < 3) {
                                escape = false;
                            }
                            List<HL7Exception> childExceptions = testField(s, field, escape);
                            for (HL7Exception ex : childExceptions) {
                                ex.setFieldPosition(i);
                            }
                            exList.addAll(childExceptions);
                        }
                    }
                } catch (HL7Exception he) {
                    exList.add(profileNotHL7Compliant(FIELD_NOT_FOUND, i));
                }
            }
            ++i;
        }
        // complain about X fields with content
        exList.addAll(checkForExtraFields(segment, allowedFields));

        for (HL7Exception ex : exList) {
            ex.setSegmentName(profile.getName());
        }
        return exList;
    }

    /**
     * Checks a segment against a list of allowed fields (ie those mentioned in the profile with
     * usage other than X). Returns a list of exceptions representing field that appear but are not
     * supposed to.
     *
     * @param allowedFields an array of Integers containing field #s of allowed fields
     */
    private List<HL7Exception> checkForExtraFields(Segment segment, List<Integer> allowedFields){
        ArrayList<HL7Exception> exList = new ArrayList<HL7Exception>();
        for (int i = 1; i <= segment.numFields(); i++) {
            if (!allowedFields.contains(new Integer(i))) {
                try {
                    Type[] reps = segment.getField(i);
                    for (Type rep : reps) {
                        CollectionUtils.addIgnoreNull(exList,
                            profileNotFollowedAssert(!rep.isEmpty(), FIELD_NOT_DEFINED_IN_PROFILE, i, segment.getName()));
                    }
                } catch (HL7Exception he) {
                    exList.add(new HL7Exception("Problem testing against profile"));
                }
            }
        }
        return exList;
    }

    protected List<HL7Exception> testField(Type type, SegmentType.Field profile, boolean escape) {
        List<HL7Exception> exList = new ArrayList<HL7Exception>();

        // account for MSH 1 & 2 which aren't escaped
        String encoded = null;
        if (!escape && Primitive.class.isAssignableFrom(type.getClass()))
            encoded = ((Primitive) type).getValue();

        exList.addAll(testType(type, profile.getDatatype(), profile.getUsage(), profile.getName(),
                      profile.getLength().intValue(), profile.getConstantValue(),encoded, false));

        // test children
        if (validateChildren) {
            if (profile.getComponents().size() > 0 && !profile.getUsage().equals("X")) {
                if (Composite.class.isAssignableFrom(type.getClass())) {
                    Composite comp = (Composite) type;
                    int i = 1;
                    for (SegmentType.Field.Component component : profile.getComponents()) {
                        try {
                            exList.addAll(testComponent(comp.getComponent(i - 1), component));
                        } catch (DataTypeException de) {
                            exList.add(profileNotHL7Compliant(COMPONENT_TYPE_MISSMATCH, type.getName(), i));
                        }
                        ++i;
                    }
                    exList.addAll(checkExtraComponents(comp, profile.getComponents().size()));
                } else {
                    exList.add(profileNotHL7Compliant(WRONG_FIELD_TYPE, type.getClass().getName()));
                }
            }
        }
        return exList;
    }

    protected List<HL7Exception> testType(Type type, String dataType, String usage, String name,
                                       int length, String constant, String encoded) {
        return testType(type, dataType, usage, name, length, constant, encoded, true);
    }

    /**
     * Tests a Type against the corresponding section of a profile.
     *
     * @param encoded optional encoded form of type (if you want to specify this -- if null, default
     *            pipe-encoded form is used to check length and constant val)
     */
    protected List<HL7Exception> testType(Type type, String dataType, String usage, String name, int length,
                                       String constant, String encoded, boolean testUsage) {
        ArrayList<HL7Exception> exList = new ArrayList<HL7Exception>();
        if (encoded == null)
            encoded = PipeParser.encode(type, this.enc);

        if (testUsage){
            HL7Exception ue = testUsage(encoded, usage, name);
            CollectionUtils.addIgnoreNull(exList, ue);
        }

        if (!usage.equals("X")) {
            // check datatype
            if ((type instanceof ca.uhn.hl7v2.model.v231.datatype.TSComponentOne
                    || type instanceof ca.uhn.hl7v2.model.v24.datatype.TSComponentOne)
                    && !dataType.equals("ST")) {

                exList.add(profileNotHL7Compliant(HL7_DATATYPE_MISSMATCH, type.getName(), dataType));
            } else if (!(type instanceof TSComponentOne) && type.getName().indexOf(dataType) < 0) {

                CollectionUtils.addIgnoreNull(exList,
                        profileNotFollowedAssert(!(type.getClass().getSimpleName().equals("Varies")
                                || type.getClass().getSimpleName().equals("QIP")),
                                HL7_DATATYPE_MISSMATCH, type.getName(), dataType));
            }

            // check length
            CollectionUtils.addIgnoreNull(exList, profileNotFollowedAssert(encoded.length() > length,
                            LENGTH_EXCEEDED, name, encoded.length(), length));

            // check constant value
            if (constant != null && constant.length() > 0) {
                CollectionUtils.addIgnoreNull(exList, profileNotFollowedAssert(!encoded.equals(constant),
                            WRONG_CONSTANT_VALUE, encoded, constant));
            }

            //TODO : check against table
            //exList.addAll(testTypeAgainstTable(type, profile));
        }

        return exList;
    }

    /**
     * Tests an element against the corresponding usage code. The element is required in its encoded
     * form.
     *
     * @param encoded the pipe-encoded message element
     * @param usage the usage code (e.g. "CE")
     * @param name the name of the element (for use in exception messages)
     * @return null if there is no problem, an HL7Exception otherwise
     */
    protected HL7Exception testUsage(String encoded, String usage, String name) {
        HL7Exception e = null;
        if (usage.equalsIgnoreCase("R")) {
            e = profileNotFollowedAssert(encoded.length() == 0, REQUIRED_ELEMENT_MISSING, name);
        } else if (usage.equalsIgnoreCase("RE")) {
            // can't test anything
        } else if (usage.equalsIgnoreCase("O")) {
            // can't test anything
        } else if (usage.equalsIgnoreCase("C")) {
            // can't test anything yet -- wait for condition syntax in v2.6
        } else if (usage.equalsIgnoreCase("CE")) {
            // can't test anything
        } else if (usage.equalsIgnoreCase("X")) {
            e = profileNotFollowedAssert(encoded.length() > 0, NOT_SUPPORTED_ELEMENT_PRESENT, name);
        } else if (usage.equalsIgnoreCase("B")) {
            // can't test anything
        }
        return e;
    }

    protected List<HL7Exception> testComponent(Type type, SegmentType.Field.Component profile) {
        List<HL7Exception> exList = new ArrayList<HL7Exception>();
        exList.addAll(testType(type, profile.getDatatype(), profile.getUsage(), profile.getName(),
                      profile.getLength().intValue(), profile.getConstantValue(), null));

        // test children
        if (profile.getSubComponents().size() > 0 && !profile.getUsage().equals("X") && hasContent(type)){
            if (Composite.class.isAssignableFrom(type.getClass())) {
                Composite comp = (Composite) type;

                if (validateChildren) {
                    int i = 1;
                    for (SegmentType.Field.Component.SubComponent subComponent : profile.getSubComponents()) {
                        try {
                            Type child = comp.getComponent(i - 1);
                            exList.addAll(testType(child, subComponent.getDatatype(), subComponent.getUsage(),
                                    subComponent.getName(), subComponent.getLength().intValue(), subComponent.getConstantValue(),null));
                        } catch (DataTypeException de) {
                            exList.add(profileNotHL7Compliant(SUBCOMPONENT_TYPE_MISSMATCH, type.getName(), i));
                        }
                        ++i;
                    }
                }

                exList.addAll(checkExtraComponents(comp, profile.getSubComponents().size()));
            } else {
                exList.add(profileNotFollowedAssert(true, WRONG_COMPONENT_TYPE, type.getClass().getName()));
            }
        }

        return exList;
    }

    /** Tests for extra components (ie any not defined in the profile) */
    private List<HL7Exception> checkExtraComponents(Composite comp, int numInProfile) {
        List<HL7Exception> exList = new ArrayList<HL7Exception>();

        StringBuffer extra = new StringBuffer();
        for (int i = numInProfile; i < comp.getComponents().length; i++) {
            try {
                String s = PipeParser.encode(comp.getComponent(i), enc);
                if (s.length() > 0) {
                    extra.append(s).append(enc.getComponentSeparator());
                }
            } catch (DataTypeException de) {
                exList.add(new HL7Exception("Problem testing against profile"));
            }
        }
        CollectionUtils.addIgnoreNull(exList,
            profileNotFollowedAssert(extra.toString().length() > 0, COMPONENT_NOT_DEFINED_IN_PROFILE, extra.toString()));

        return exList;
    }

    /** Returns true is there is content in the given type */
    private boolean hasContent(Type type) {
        boolean has = false;
        String encoded = PipeParser.encode(type, enc);
        if (encoded != null && encoded.length() > 0)
            has = true;
        return has;
    }

}

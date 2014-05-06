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

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.*;
import ca.uhn.hl7v2.model.primitive.TSComponentOne;
import ca.uhn.hl7v2.parser.EncodingCharacters;
import ca.uhn.hl7v2.parser.PipeParser;
import ca.uhn.hl7v2.util.Terser;
import ca.uhn.hl7v2.validation.ValidationException;
import ca.uhn.hl7v2.validation.impl.AbstractMessageRule;
import org.openehealth.ipf.gazelle.validation.core.stub.HL7V2XConformanceProfile;
import org.openehealth.ipf.gazelle.validation.core.stub.HL7V2XStaticDef;
import org.openehealth.ipf.gazelle.validation.core.stub.SegmentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.openehealth.ipf.gazelle.validation.core.util.MessageUtils.*;
import static org.openehealth.ipf.gazelle.validation.core.util.ProfileAssertions.profileNotHL7Compliant;
import static org.openehealth.ipf.gazelle.validation.core.util.ProfileAssertions.profileViolatedWhen;
import static org.openehealth.ipf.gazelle.validation.core.util.ProfileValidationMessage.*;

/**
 * A modified conformance profile validator from HAPI. This implementation differs from HAPI's
 * {@link ca.uhn.hl7v2.conf.check.DefaultValidator} and cannot be returned by {@link ca.uhn.hl7v2.HapiContext#getConformanceValidator()}
 * due to its different signature.
 */
class GazelleProfileRule extends AbstractMessageRule {

    private EncodingCharacters enc;
    private static final Logger LOG = LoggerFactory.getLogger(GazelleProfileRule.class);
    private HL7V2XConformanceProfile profile;
    private boolean validateChildren = true;

    public GazelleProfileRule(HL7V2XConformanceProfile profile) {
        enc = new EncodingCharacters('|', null); // the | is assumed later -- don't change
        this.profile = profile;
    }

    /**
     * If set to false (default is true), each test method will omit child validation
     */
    public void setValidateChildren(boolean validateChildren) {
        this.validateChildren = validateChildren;
    }


    @Override
    public ValidationException[] apply(Message message) {
        List<ValidationException> violations = new ArrayList<ValidationException>();

        HL7V2XStaticDef staticDef = null;
        for (Object ref : profile.getDynamicDevesAndHL7V2XStaticDevesAndHL7V2XStaticDefReves()) {
            if (ref.getClass().isAssignableFrom(HL7V2XStaticDef.class)) {
                staticDef = (HL7V2XStaticDef) ref;
            }
        }

        if (staticDef == null) {
            violations.add(new ValidationException("No Static Definitions found in HL7V2XConformance profile"));
        } else {
            Terser terser = new Terser(message);
            checkMSHTypeField(staticDef.getMsgType(), terser, violations);
            checkMSHEventField(staticDef.getEventType(), terser, violations);
            checkMSHStructureField(staticDef.getMsgStructID(), terser, violations);
            checkMSHVersionField(profile.getHL7Version(), terser, violations);
            violations.addAll(testGroup(message, staticDef.getSegmentsAndSegGroups()));
        }
        return violations.toArray(new ValidationException[violations.size()]);
    }


    protected List<ValidationException> testGroup(Group group, List<Object> profile) {
        List<ValidationException> exList = new ArrayList<ValidationException>();
        List<String> allowedStructures = new ArrayList<String>();
        for (Object struct : profile) {
            String name = null;
            String usage = "";
            int max = 0;
            int min = 0;
            if (struct.getClass().isAssignableFrom(SegmentType.class)) {
                SegmentType structure = (SegmentType) struct;
                usage = structure.getUsage();
                name = structure.getName();
                max = structure.getMax().equals("*") ? Short.MAX_VALUE : Short.valueOf(structure.getMax());
                min = structure.getMin().intValue();
            } else if (struct.getClass().isAssignableFrom(HL7V2XStaticDef.SegGroup.class)) {
                HL7V2XStaticDef.SegGroup segGroup = (HL7V2XStaticDef.SegGroup) struct;
                usage = segGroup.getUsage();
                name = segGroup.getName();
                max = segGroup.getMax().equals("*") ? Short.MAX_VALUE : Short.valueOf(segGroup.getMax());
                min = segGroup.getMin().intValue();
            }

            if (!usage.equalsIgnoreCase("X")) {
                allowedStructures.add(name);

                try {
                    List<Structure> nonEmptyStructures = new ArrayList<Structure>();
                    Structure[] structures = group.getAll(name);
                    for (Structure structure : structures) {
                        if (!structure.isEmpty())
                            nonEmptyStructures.add(structure);
                    }
                    exList.addAll(testCardinality(nonEmptyStructures.size(), min, max, usage, name));

                    // test children on instances with content
                    if (validateChildren) {
                        for (Structure structure : nonEmptyStructures) {
                            List<ValidationException> childExceptions = testStructure(structure, struct);
                            exList.addAll(childExceptions);
                        }
                    }
                } catch (HL7Exception he) {
                    profileNotHL7Compliant(exList, PROFILE_STRUCTURE_NOT_EXIST_IN_JAVA_CLASS, name);
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
    protected List<ValidationException> checkForExtraStructures(Group group, List<String> allowedStructures) {
        List<ValidationException> exList = new ArrayList<ValidationException>();
        for (String childName : group.getNames()) {
            if (!allowedStructures.contains(childName)) {
                try {
                    for (Structure rep : group.getAll(childName)) {
                        profileViolatedWhen(!rep.isEmpty(), exList, STRUCTURE_NOT_DEFINED_IN_PROFILE, childName);
                    }
                } catch (HL7Exception he) {
                    exList.add(new ValidationException("Problem checking profile:" + he.getMessage()));
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
     * @param reps  the number of reps
     * @param min   the minimum number of reps
     * @param max   the maximum number of reps (-1 means *)
     * @param usage the usage code
     * @param name  the name of the repeating structure (used in exception msg)
     * @return null if cardinality OK, exception otherwise
     */
    protected List<ValidationException> testCardinality(int reps, int min, int max, String usage, String name) {
        List<ValidationException> violations = new ArrayList<ValidationException>();
        profileViolatedWhen(reps < min && usage.equalsIgnoreCase("R"),
                violations, LESS_THAN_MINIMUM_CARDINALITY, name, min, reps);

        profileViolatedWhen(max > 0 && reps > max,
                violations, MORE_THAN_MAXIMUM_CARDINALITY, name, max, reps);

        profileViolatedWhen(reps > 0 && usage.equalsIgnoreCase("X"),
                violations, NOT_SUPPORTED_ELEMENT_PRESENT, name);

        return violations;
    }


    /**
     * Tests a structure (segment or group) against the corresponding part of a profile.
     */
    protected List<ValidationException> testStructure(Structure s, Object profile) {
        List<ValidationException> exList = new ArrayList<ValidationException>();
        if (profile instanceof SegmentType) {
            if (Segment.class.isAssignableFrom(s.getClass())) {
                exList.addAll(testSegment((Segment) s, (SegmentType) profile));
            } else {
                profileNotHL7Compliant(exList, PROFILE_STRUCTURE_MISMATCH,
                        "segment", s.getClass().getName());
            }
        } else if (profile instanceof HL7V2XStaticDef.SegGroup) {
            if (Group.class.isAssignableFrom(s.getClass())) {
                exList.addAll(testGroup((Group) s, ((HL7V2XStaticDef.SegGroup) profile).getSegGroupsAndSegments()));
            } else {
                profileNotHL7Compliant(exList, PROFILE_STRUCTURE_MISMATCH,
                        "group", s.getClass().getName());
            }
        }
        return exList;
    }


    protected List<ValidationException> testSegment(Segment segment, SegmentType profile) {
        List<ValidationException> exList = new ArrayList<ValidationException>();
        List<Integer> allowedFields = new ArrayList<Integer>();
        int i = 1;
        for (SegmentType.Field field : profile.getFields()) {

            // only test a field in detail if it isn't X
            if (!field.getUsage().equalsIgnoreCase("X")) {
                allowedFields.add(i);

                // see which instances have content
                try {
                    Type[] fields = segment.getField(i);
                    List<Type> nonEmptyFields = new ArrayList<Type>();
                    for (Type f : fields) {
                        if (!f.isEmpty())
                            nonEmptyFields.add(f);
                    }

                    int max = field.getMax().equals("*") ? Short.MAX_VALUE : Short.valueOf(field.getMax());
                    exList.addAll(
                            testCardinality(nonEmptyFields.size(), field.getMin().intValue(),
                                    max, field.getUsage(), field.getName())
                    );

                    // test field instances with content
                    if (validateChildren) {
                        for (Type type : nonEmptyFields) {
                            boolean escape = true; // escape field value when checking length
                            if (profile.getName().equalsIgnoreCase("MSH") && i < 3) {
                                escape = false;
                            }
                            List<ValidationException> childExceptions = testField(type, field, escape);
                            for (ValidationException ex : childExceptions) {
                                ex.setFieldPosition(i);
                            }
                            exList.addAll(childExceptions);
                        }
                    }
                } catch (HL7Exception he) {
                    profileNotHL7Compliant(exList, FIELD_NOT_FOUND, i);
                }
            }
            ++i;
        }
        // complain about X fields with content
        exList.addAll(checkForExtraFields(segment, allowedFields));

        for (ValidationException ex : exList) {
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
    protected List<ValidationException> checkForExtraFields(Segment segment, List<Integer> allowedFields) {
        ArrayList<ValidationException> exList = new ArrayList<ValidationException>();
        for (int i = 1; i <= segment.numFields(); i++) {
            if (!allowedFields.contains(new Integer(i))) {
                try {
                    Type[] reps = segment.getField(i);
                    for (Type rep : reps) {
                        profileViolatedWhen(!rep.isEmpty(), exList, FIELD_NOT_DEFINED_IN_PROFILE, i, segment.getName());
                    }
                } catch (HL7Exception he) {
                    exList.add(new ValidationException("Problem testing against profile: " + he.getMessage()));
                }
            }
        }
        return exList;
    }

    protected List<ValidationException> testField(Type type, SegmentType.Field profile, boolean escape) {
        List<ValidationException> exList = new ArrayList<ValidationException>();

        // account for MSH 1 & 2 which aren't escaped
        String encoded = null;
        if (!escape && Primitive.class.isAssignableFrom(type.getClass()))
            encoded = ((Primitive) type).getValue();

        exList.addAll(testType(type, profile.getDatatype(), profile.getUsage(), profile.getName(),
                profile.getLength().intValue(), profile.getConstantValue(), encoded, false));

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
                            profileNotHL7Compliant(exList, COMPONENT_TYPE_MISSMATCH, type.getName(), i);
                        }
                        ++i;
                    }
                    exList.addAll(checkUndefinedComponents(comp, profile.getComponents().size()));
                } else {
                    profileNotHL7Compliant(exList, WRONG_FIELD_TYPE, type.getClass().getName());
                }
            }
        }
        return exList;
    }

    protected List<ValidationException> testType(Type type, String dataType, String usage, String name,
                                                 int length, String constant, String encoded) {
        return testType(type, dataType, usage, name, length, constant, encoded, true);
    }

    /**
     * Tests a Type against the corresponding section of a profile.
     *
     * @param encoded optional encoded form of type (if you want to specify this -- if null, default
     *                pipe-encoded form is used to check length and constant val)
     */
    protected List<ValidationException> testType(Type type, String dataType, String usage, String name, int length,
                                                 String constant, String encoded, boolean testUsage) {
        ArrayList<ValidationException> exList = new ArrayList<ValidationException>();
        if (encoded == null)
            encoded = PipeParser.encode(type, this.enc);

        if (testUsage) {
            testUsage(exList, encoded, usage, name);
        }

        if (!usage.equals("X")) {
            // check datatype
            if ((type instanceof ca.uhn.hl7v2.model.v231.datatype.TSComponentOne
                    || type instanceof ca.uhn.hl7v2.model.v24.datatype.TSComponentOne)
                    && !dataType.equals("ST")) {

                profileNotHL7Compliant(exList, HL7_DATATYPE_MISSMATCH, type.getName(), dataType);
            } else if (!(type instanceof TSComponentOne) && !type.getName().contains(dataType)) {

                profileViolatedWhen(!(type.getClass().getSimpleName().equals("Varies")
                                || type.getClass().getSimpleName().equals("QIP")),
                        exList, HL7_DATATYPE_MISSMATCH, type.getName(), dataType
                );
            }

            // check length
            profileViolatedWhen(encoded.length() > length,
                    exList, LENGTH_EXCEEDED, name, encoded.length(), length);

            // check constant value
            if (constant != null && constant.length() > 0) {
                profileViolatedWhen(!encoded.equals(constant),
                        exList, WRONG_CONSTANT_VALUE, encoded, constant);
            }

            //TODO : check against table, or do we need this check?
        }

        return exList;
    }

    /**
     * Tests an element against the corresponding usage code. The element is required in its encoded
     * form.
     *
     * @param encoded the pipe-encoded message element
     * @param usage   the usage code (e.g. "CE")
     * @param name    the name of the element (for use in exception messages)
     * @return null if there is no problem, an HL7Exception otherwise
     */
    protected HL7Exception testUsage(List<ValidationException> exList, String encoded, String usage, String name) {
        if (usage.equalsIgnoreCase("R")) {
            profileViolatedWhen(encoded.length() == 0, exList, REQUIRED_ELEMENT_MISSING, name);
        }
        /*
        else if (usage.equalsIgnoreCase("RE")) {
            // can't test anything
        } else if (usage.equalsIgnoreCase("O")) {
            // can't test anything
        } else if (usage.equalsIgnoreCase("C")) {
            // can't test anything yet -- wait for condition syntax in v2.6
        } else if (usage.equalsIgnoreCase("CE")) {
            // can't test anything
        } else if (usage.equalsIgnoreCase("X")) {
            e = profileViolatedWhen(encoded.length() > 0, NOT_SUPPORTED_ELEMENT_PRESENT, name);
        } else if (usage.equalsIgnoreCase("B")) {
            // can't test anything
        }
        */
        return null;
    }

    protected List<ValidationException> testComponent(Type type, SegmentType.Field.Component profile) {
        List<ValidationException> exList = new ArrayList<ValidationException>();
        exList.addAll(testType(type, profile.getDatatype(), profile.getUsage(), profile.getName(),
                profile.getLength().intValue(), profile.getConstantValue(), null));

        // test children
        try {
            if (profile.getSubComponents().size() > 0 && !profile.getUsage().equals("X") && hasContent(type)) {
                if (Composite.class.isAssignableFrom(type.getClass())) {
                    Composite comp = (Composite) type;

                    if (validateChildren) {
                        int i = 1;
                        for (SegmentType.Field.Component.SubComponent subComponent : profile.getSubComponents()) {
                            try {
                                Type sub = comp.getComponent(i - 1);
                                exList.addAll(testType(sub, subComponent.getDatatype(), subComponent.getUsage(),
                                        subComponent.getName(), subComponent.getLength().intValue(),
                                        subComponent.getConstantValue(), null));
                            } catch (DataTypeException de) {
                                profileNotHL7Compliant(exList, SUBCOMPONENT_TYPE_MISSMATCH, type.getName(), i);
                            }
                            ++i;
                        }
                    }

                    exList.addAll(checkUndefinedComponents(comp, profile.getSubComponents().size()));
                } else {
                    profileViolatedWhen(true, exList, WRONG_COMPONENT_TYPE, type.getClass().getName());
                }
            }
        } catch (HL7Exception e) {
            exList.add(new ValidationException(e));
        }

        return exList;
    }

    /**
     * Tests for extra components (i.e. any not defined in the profile)
     */
    protected List<ValidationException> checkUndefinedComponents(Composite comp, int numInProfile) {
        List<ValidationException> exList = new ArrayList<ValidationException>();

        StringBuilder extra = new StringBuilder();
        for (int i = numInProfile; i < comp.getComponents().length; i++) {
            try {
                String s = comp.getComponent(i).encode();
                if (s.length() > 0) {
                    extra.append(s).append(enc.getComponentSeparator());
                }
            } catch (HL7Exception de) {
                exList.add(new ValidationException(de));
            }
        }
        profileViolatedWhen(extra.toString().length() > 0, exList, COMPONENT_NOT_DEFINED_IN_PROFILE, extra.toString());

        return exList;
    }

    /**
     * Returns true is there is content in the given type
     */
    protected boolean hasContent(Type type) throws HL7Exception {
        return !type.isEmpty();
    }

}

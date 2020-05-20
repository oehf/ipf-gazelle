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


import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.*;
import ca.uhn.hl7v2.model.primitive.TSComponentOne;
import ca.uhn.hl7v2.parser.EncodingCharacters;
import ca.uhn.hl7v2.parser.PipeParser;
import ca.uhn.hl7v2.util.Terser;
import ca.uhn.hl7v2.validation.ValidationException;
import ca.uhn.hl7v2.validation.impl.AbstractMessageRule;
import org.apache.commons.beanutils.BeanUtils;
import org.openehealth.ipf.gazelle.validation.core.stub.HL7V2XConformanceProfile;
import org.openehealth.ipf.gazelle.validation.core.stub.HL7V2XStaticDef;
import org.openehealth.ipf.gazelle.validation.core.stub.SegmentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static org.openehealth.ipf.gazelle.validation.core.util.MessageUtils.*;
import static org.openehealth.ipf.gazelle.validation.core.util.ProfileAssertions.profileNotHL7Compliant;
import static org.openehealth.ipf.gazelle.validation.core.util.ProfileAssertions.profileViolatedWhen;
import static org.openehealth.ipf.gazelle.validation.core.util.ProfileValidationMessage.*;

/**
 * A modified conformance profile validator from HAPI. This implementation differs from HAPI's
 * {@link ca.uhn.hl7v2.conf.check.DefaultValidator} and cannot be returned by {@link ca.uhn.hl7v2.HapiContext#getConformanceValidator()}
 * due to its different signature.
 */
public class GazelleProfileRule extends AbstractMessageRule {

    private final EncodingCharacters enc;
    private static final Logger LOG = LoggerFactory.getLogger(GazelleProfileRule.class);
    private final HL7V2XConformanceProfile profile;
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
        List<ValidationException> violations = new ArrayList<>();

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
            // checkMSHStructureField(staticDef.getMsgStructID(), terser, violations);
            checkMSHVersionField(profile.getHL7Version(), terser, violations);
            violations.addAll(testGroup(message, staticDef.getSegmentsAndSegGroups()));
        }
        return violations.toArray(new ValidationException[0]);
    }

    /**
     * Tests the group (or message) against a list of profile descriptions that are either a
     * {@link org.openehealth.ipf.gazelle.validation.core.stub.SegmentType} or a
     * {@link org.openehealth.ipf.gazelle.validation.core.stub.HL7V2XStaticDef.SegGroup}.
     *
     * @param group   current message/group element
     * @param profile available profile objcts to test the element against
     * @return a list with identified violations against the profile(s)
     */
    protected List<ValidationException> testGroup(Group group, List<Object> profile) {
        List<ValidationException> exList = new ArrayList<>();
        List<String> allowedStructures = new ArrayList<>();

        for (Object struct : profile) {
            UsageInfo usage = new UsageInfo(struct);

            if (!usage.disallowed()) {
                allowedStructures.add(usage.name);

                try {
                    List<Structure> nonEmptyStructures = nonEmptyStructure(group.getAll(usage.name));
                    exList.addAll(testCardinality(nonEmptyStructures.size(), usage));

                    // test children on instances with content
                    if (validateChildren) {
                        for (Structure structure : nonEmptyStructures) {
                            exList.addAll(testStructure(structure, struct));
                        }
                    }
                } catch (HL7Exception he) {
                    profileNotHL7Compliant(exList, PROFILE_STRUCTURE_NOT_EXIST_IN_JAVA_CLASS, usage.name);
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
        List<ValidationException> exList = new ArrayList<>();
        for (String childName : group.getNames()) {
            if (!allowedStructures.contains(childName)) {
                try {
                    for (Structure rep : group.getAll(childName)) {
                        profileViolatedWhen(!isEmpty(rep), exList, STRUCTURE_NOT_DEFINED_IN_PROFILE, childName);
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
     * @param usage usage info
     * @return exceptions
     */
    protected List<ValidationException> testCardinality(int reps, UsageInfo usage) {
        List<ValidationException> violations = new ArrayList<>();
        profileViolatedWhen(reps < usage.min && usage.required(),
                violations, LESS_THAN_MINIMUM_CARDINALITY, usage.name, usage.min, reps);
        profileViolatedWhen(usage.max > 0 && reps > usage.max,
                violations, MORE_THAN_MAXIMUM_CARDINALITY, usage.name, usage.max, reps);
        profileViolatedWhen(reps > 0 && usage.disallowed(),
                violations, NOT_SUPPORTED_ELEMENT_PRESENT, usage.name);
        return violations;
    }


    /**
     * Tests a structure (segment or group) against the corresponding part of a profile.
     */
    protected List<ValidationException> testStructure(Structure s, Object profile) {
        List<ValidationException> exList = new ArrayList<>();
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
        List<ValidationException> exList = new ArrayList<>();
        List<Integer> allowedFields = new ArrayList<>();
        int i = 1;
        for (SegmentType.Field field : profile.getFields()) {
            UsageInfo usage = new UsageInfo(field);
            // only test a field in detail if it isn't X
            if (!usage.disallowed()) {
                allowedFields.add(i);

                // see which instances have content
                try {
                    Collection<Type> nonEmptyFields = nonEmptyField(segment.getField(i));
                    exList.addAll(testCardinality(nonEmptyFields.size(), usage));

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
        ArrayList<ValidationException> exList = new ArrayList<>();
        for (int i = 1; i <= segment.numFields(); i++) {
            if (!allowedFields.contains(i)) {
                try {
                    Type[] reps = segment.getField(i);
                    for (Type rep : reps) {
                        profileViolatedWhen(!isEmpty(rep), exList, FIELD_NOT_DEFINED_IN_PROFILE, i, segment.getName());
                    }
                } catch (HL7Exception he) {
                    exList.add(new ValidationException("Problem testing against profile: " + he.getMessage()));
                }
            }
        }
        return exList;
    }

    protected List<ValidationException> testField(Type type, SegmentType.Field profile, boolean escape) {

        UsageInfo usage = new UsageInfo(profile);

        // account for MSH 1 & 2 which aren't escaped
        String encoded = null;
        if (!escape && Primitive.class.isAssignableFrom(type.getClass()))
            encoded = ((Primitive) type).getValue();

        List<ValidationException> exList = new ArrayList<>(testType(type, profile.getDatatype(), usage, encoded, false));

        // test children
        if (validateChildren) {
            if (profile.getComponents().size() > 0 && !usage.disallowed()) {
                if (Composite.class.isAssignableFrom(type.getClass())) {
                    Composite comp = (Composite) type;
                    int i = 1;
                    boolean nullContext = false;
                    for (SegmentType.Field.Component component : profile.getComponents()) {
                        try {
                            SegmentType.Field.Component component2;
                            if (nullContext) {
                                component2 = new SegmentType.Field.Component();
                                try {
                                    BeanUtils.copyProperties(component2, component);
                                } catch (InvocationTargetException | IllegalAccessException e) {
                                    // nop
                                }
                                component2.setUsage("NULL");
                            } else {
                                component2 = component;
                                if ((i == 1) && profile.isNullable() &&
                                        PipeParser.encode(comp.getComponent(0), this.enc).equals("\"\""))
                                {
                                    nullContext = true;
                                }
                            }

                            exList.addAll(testComponent(comp.getComponent(i - 1), component2));
                        } catch (DataTypeException de) {
                            profileNotHL7Compliant(exList, COMPONENT_TYPE_MISMATCH, type.getName(), i);
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

    protected List<ValidationException> testType(Type type, String dataType, UsageInfo usage, String encoded) {
        return testType(type, dataType, usage, encoded, true);
    }

    /**
     * Tests a Type against the corresponding section of a profile.
     *
     * @param encoded optional encoded form of type (if you want to specify this -- if null, default
     *                pipe-encoded form is used to check length and constant val)
     */
    protected List<ValidationException> testType(Type type, String dataType, UsageInfo usage, String encoded, boolean testUsage) {
        ArrayList<ValidationException> exList = new ArrayList<>();
        if (encoded == null)
            encoded = PipeParser.encode(type, this.enc);

        if (testUsage) {
            testUsage(exList, encoded, usage);
        }

        if (!usage.disallowed() && !encoded.isEmpty()) {
            // check datatype
            if ((type instanceof ca.uhn.hl7v2.model.v231.datatype.TSComponentOne
                    || type instanceof ca.uhn.hl7v2.model.v24.datatype.TSComponentOne)
                    && !dataType.equals("ST")) {
                profileNotHL7Compliant(exList, HL7_DATATYPE_MISMATCH, type.getName(), dataType);
            } else if (!(type instanceof TSComponentOne) && !type.getName().contains(dataType)) {
                profileViolatedWhen(!(type.getClass().getSimpleName().equals("Varies")
                                || type.getClass().getSimpleName().equals("QIP")),
                        exList, HL7_DATATYPE_MISMATCH, type.getName(), dataType
                );
            }

            // check length
            profileViolatedWhen(encoded.length() > usage.length,
                    exList, LENGTH_EXCEEDED, usage.name, encoded.length(), usage.length);

            // check constant value
            if (usage.constantValue != null && usage.constantValue.length() > 0) {
                profileViolatedWhen(!encoded.equals(usage.constantValue),
                        exList, WRONG_CONSTANT_VALUE, encoded, usage.constantValue);
            }

            // TODO : check against table, or do we need this check?
            // Gazelle checks code system and issues a WARNING if a check fails
        }

        return exList;
    }

    /**
     * Tests an element against the corresponding usage code. The element is required in its encoded
     * form.
     *
     * @param encoded the pipe-encoded message element
     * @param usage   the usage code (e.g. "CE")
     */
    protected void testUsage(List<ValidationException> exList, String encoded, UsageInfo usage) {
        if (usage.required()) {
            profileViolatedWhen(encoded.isEmpty(), exList, REQUIRED_ELEMENT_MISSING, usage.name);
        } else if (usage.disallowed()) {
            profileViolatedWhen(!encoded.isEmpty(), exList, NOT_SUPPORTED_ELEMENT_PRESENT, usage.name);
        } else if (usage.nullContext()) {
            profileViolatedWhen(!encoded.isEmpty(), exList, NO_ELEMENTS_AFTER_NULL, usage.name);
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
        } else if (usage.equalsIgnoreCase("B")) {
            // can't test anything
        }
        */
    }

    protected List<ValidationException> testComponent(Type type, SegmentType.Field.Component profile) {
        UsageInfo usage = new UsageInfo(profile);
        List<ValidationException> exList = new ArrayList<>(testType(type, profile.getDatatype(), usage, null));

        // test children
        try {
            if (profile.getSubComponents().size() > 0 && !usage.disallowed() && !isEmpty(type)) {
                if (Composite.class.isAssignableFrom(type.getClass())) {
                    Composite comp = (Composite) type;

                    if (validateChildren) {
                        int i = 1;
                        for (SegmentType.Field.Component.SubComponent subComponent : profile.getSubComponents()) {
                            UsageInfo scUsage = new UsageInfo(subComponent);
                            try {
                                Type sub = comp.getComponent(i - 1);
                                exList.addAll(testType(sub, subComponent.getDatatype(), scUsage, null));
                            } catch (DataTypeException de) {
                                profileNotHL7Compliant(exList, SUBCOMPONENT_TYPE_MISMATCH, type.getName(), i);
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
        List<ValidationException> exList = new ArrayList<>();

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


    @SafeVarargs
    private static <T extends Structure> List<T> nonEmptyStructure(T... input) throws HL7Exception {
        List<T> result = new ArrayList<>();
        if (input != null) {
            for (T element : input) {
                if (!isEmpty(element)) result.add(element);
            }
        }
        return result;
    }

    // In contrast to {@link #nonEmptyStructure, this will only remove trailing empty fields.
    // If all fields are empty, an empty list is returned
    @SafeVarargs
    private static <T extends Type> Collection<T> nonEmptyField(T... input) throws HL7Exception {
        if (input == null || input.length == 0) return Collections.emptySet();
        if (input.length == 1) return isEmpty(input[0]) ? Collections.emptySet() : Collections.singleton(input[0]);

        boolean seenNonEmptyRepetition = false;
        List<T> result = new ArrayList<>();
        for (T element : input) {
            boolean isEmpty = isEmpty(element);
            if (!(isEmpty && seenNonEmptyRepetition)) {
                seenNonEmptyRepetition = result.add(element);
            }
        }
        return result;
    }

    // Work around HAPI #224: TSComponentOne implementation of isEmpty is buggy
    private static boolean isEmpty(Visitable v) throws HL7Exception {
        if (v == null) return true;
        if (v instanceof TSComponentOne) {
            TSComponentOne tsc1 = (TSComponentOne) v;
            return tsc1.getValue() == null || tsc1.getValue().isEmpty();
        }
        if (v instanceof Composite && v.getClass().getName().endsWith(".TS")) {
            Composite ts = (Composite)v;
            return isEmpty(ts.getComponent(0));
        }
        return v.isEmpty();
    }

}

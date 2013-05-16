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
import ca.uhn.hl7v2.conf.check.ProfileNotFollowedException;
import ca.uhn.hl7v2.conf.check.ProfileNotHL7CompliantException;
import ca.uhn.hl7v2.conf.check.Validator;
import ca.uhn.hl7v2.conf.check.XElementPresentException;
import ca.uhn.hl7v2.model.primitive.TSComponentOne;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.uhn.hl7v2.conf.ProfileException;
import ca.uhn.hl7v2.conf.spec.message.AbstractComponent;
import ca.uhn.hl7v2.conf.spec.message.AbstractSegmentContainer;
import ca.uhn.hl7v2.conf.spec.message.Component;
import ca.uhn.hl7v2.conf.spec.message.Field;
import ca.uhn.hl7v2.conf.spec.message.ProfileStructure;
import ca.uhn.hl7v2.conf.spec.message.Seg;
import ca.uhn.hl7v2.conf.spec.message.SegGroup;
import ca.uhn.hl7v2.conf.spec.message.StaticDef;
import ca.uhn.hl7v2.conf.spec.message.SubComponent;
import ca.uhn.hl7v2.conf.store.CodeStore;
import ca.uhn.hl7v2.conf.store.ProfileStoreFactory;
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

import static org.openehealth.ipf.gazelle.validation.core.util.MessageUtils.*;
/**
 * A slightly modified conformance profile validator from HAPI
 * to comply the Gazelle GUI Client validation results..
 *
 * Note: this class is currently NOT thread-safe!
 *
 */
public class GazelleProfileValidator extends HapiContextSupport implements ProfileValidator {

    private EncodingCharacters enc; // used to check for content in parts of a message
    private static final Logger log = LoggerFactory.getLogger(GazelleProfileValidator.class);
    private boolean validateChildren = true;
    private CodeStore codeStore;

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

    /**
     * <p>
     * Provides a code store to use to provide the code tables which will be used to validate coded
     * value types. If a code store has not been set (which is the default),
     * {@link ProfileStoreFactory} will be checked for an appropriate code store, and if none is
     * found then coded values will not be validated.
     * </p>
     */
    public void setCodeStore(CodeStore theCodeStore) {
        codeStore = theCodeStore;
    }

    /**
     *
     * @see ProfileValidator#validate
     */
    @Override
    public HL7Exception[] validate(Message message, StaticDef profile, GazelleProfile gazelleProfile)
            throws ProfileException, HL7Exception {
        List<HL7Exception> exList = new ArrayList<HL7Exception>();
        Terser terser = new Terser(message);

        // check msg type, event type, msg struct ID, version
        String msgType = messageType(terser);
        if (!msgType.equals(gazelleProfile.type())) {
            HL7Exception e = new ProfileNotFollowedException("Message type " + msgType
                    + " doesn't match profile type of " + gazelleProfile.type());
            e.setError(ErrorCode.UNSUPPORTED_MESSAGE_TYPE);
            exList.add(e);
        }

        String evType = triggerEvent(terser);
        if (!evType.equals(gazelleProfile.event())
                && !gazelleProfile.event().equalsIgnoreCase("ALL")) {
            HL7Exception e = new ProfileNotFollowedException("Event type " + evType
                    + " doesn't match profile type of " + gazelleProfile.event());
            e.setError(ErrorCode.UNSUPPORTED_EVENT_CODE);
            exList.add(e);
        }

        String msgStruct = messageStructure(terser);
        if (msgStruct == null || !msgStruct.equals(gazelleProfile.structure())) {
            HL7Exception e = new ProfileNotFollowedException("Message structure " + msgStruct
                    + " doesn't match profile structure of " + gazelleProfile.structure());
            e.setError(ErrorCode.UNSUPPORTED_PROCESSING_ID);
            exList.add(e);
        }

        String msgVersion = message.getVersion();
        if (msgVersion == null || !msgVersion.equals(gazelleProfile.hl7version())) {
            HL7Exception e = new ProfileNotFollowedException("Message version " + msgVersion
                    + " doesn't match profile version of " + gazelleProfile.hl7version());
            e.setError(ErrorCode.UNSUPPORTED_VERSION_ID);
            exList.add(e);
        }

        exList.addAll(testGroup(message, profile, profile.getIdentifier()));
        return exList.toArray(new HL7Exception[exList.size()]);
    }

    /**
     * @see Validator#validate
     */
    public HL7Exception[] validate(Message message, StaticDef profile) throws ProfileException,
            HL7Exception {
        return validate(message, profile, guessGazelleProfile(message));
    }

    private List<HL7Exception> testGroup(Group group, AbstractSegmentContainer profile, String profileID) throws ProfileException {
        List<HL7Exception> exList = new ArrayList<HL7Exception>();
        List<String> allowedStructures = new ArrayList<String>();

        for (ProfileStructure struct : profile) {

            // only test a structure in detail if it isn't X
            if (!struct.getUsage().equalsIgnoreCase("X")) {
                allowedStructures.add(struct.getName());

                // see which instances have content
                try {
                    List<Structure> instancesWithContent = new ArrayList<Structure>();
                    for (Structure instance : group.getAll(struct.getName())) {
                        if (!instance.isEmpty())
                            instancesWithContent.add(instance);
                    }

                    HL7Exception ce = testCardinality(instancesWithContent.size(), struct.getMin(),
                            struct.getMax(), struct.getUsage(), struct.getName(), ErrorCode.SEGMENT_SEQUENCE_ERROR);
                    if (ce != null)
                        exList.add(ce);

                    // test children on instances with content
                    if (validateChildren) {
                        for (Structure s : instancesWithContent) {
                            List<HL7Exception> childExceptions = testStructure(s, struct, profileID);
                            exList.addAll(childExceptions);
                        }
                    }

                } catch (HL7Exception he) {
                    HL7Exception exc = new ProfileNotHL7CompliantException(struct.getName() +
                            " is expected by the message profile but does not appear in the Java class" +
                            " representing the message structure");
                    exc.setSeverity(Severity.INFO);
                    exList.add(exc);
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
    private List<HL7Exception> checkForExtraStructures(Group group, List<String> allowedStructures)
            throws ProfileException {
        List<HL7Exception> exList = new ArrayList<HL7Exception>();
        for (String childName : group.getNames()) {
            if (!allowedStructures.contains(childName)) {
                try {
                    for (Structure rep : group.getAll(childName)) {
                        if (!rep.isEmpty()) {
                            HL7Exception e = new XElementPresentException("The structure "
                                    + childName + " appears in the message but not in the profile");
                            exList.add(e);
                        }
                    }
                } catch (HL7Exception he) {
                    throw new ProfileException("Problem checking profile", he);
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
    protected HL7Exception testCardinality(int reps, int min, int max, String usage, String name, ErrorCode errorCode) {
        HL7Exception e = null;
        if (reps > 0 && usage.equalsIgnoreCase("X")) {
            e = new ProfileNotFollowedException("Element '" + name
                    + "' is present in the message but specified as not used (X) by the profile");
            e.setError(errorCode);
            e.setSeverity(Severity.WARNING);
        } else if (reps < min && usage.equalsIgnoreCase("R")) {
            e = new ProfileNotFollowedException(name + " must have at least " + min
                    + " repetitions (has " + reps + ")");
            e.setError(errorCode);
        } else if (max > 0 && reps > max) {
            e = new ProfileNotFollowedException(name + " must have no more than " + max
                    + " repetitions (has " + reps + ")");
            e.setError(errorCode);
        }
        return e;
    }

    /**
     * Tests a structure (segment or group) against the corresponding part of a profile.
     */
    public List<HL7Exception> testStructure(Structure s, ProfileStructure profile, String profileID)
            throws ProfileException {
        List<HL7Exception> exList = new ArrayList<HL7Exception>();
        if (profile instanceof Seg) {
            if (Segment.class.isAssignableFrom(s.getClass())) {
                exList.addAll(testSegment((Segment) s, (Seg) profile, profileID));
            } else {
                HL7Exception e = new ProfileNotHL7CompliantException(
                        "Mismatch between a segment in the profile and the structure "
                                + s.getClass().getName() + " in the message");
                e.setError(ErrorCode.APPLICATION_INTERNAL_ERROR);
                exList.add(e);
            }
        } else if (profile instanceof SegGroup) {
            if (Group.class.isAssignableFrom(s.getClass())) {
                exList.addAll(testGroup((Group) s, (SegGroup) profile, profileID));
            } else {
                exList.add(new ProfileNotHL7CompliantException(
                        "Mismatch between a group in the profile and the structure "
                                + s.getClass().getName() + " in the message"));
            }
        }
        return exList;
    }

    private List<HL7Exception> testSegment(ca.uhn.hl7v2.model.Segment segment, Seg profile,
                                             String profileID) throws ProfileException {
        List<HL7Exception> exList = new ArrayList<HL7Exception>();
        List<Integer> allowedFields = new ArrayList<Integer>();

        for (int i = 1; i <= profile.getFields(); i++) {
            Field field = profile.getField(i);

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

                    HL7Exception ce = testCardinality(instancesWithContent.size(), field.getMin(),
                            field.getMax(), field.getUsage(), field.getName(), ErrorCode.REQUIRED_FIELD_MISSING);
                    if (ce != null) {
                        ce.setFieldPosition(i);
                        exList.add(ce);
                    }

                    // test field instances with content
                    if (validateChildren) {
                        for (Type s : instancesWithContent) {
                            boolean escape = true; // escape field value when checking length
                            if (profile.getName().equalsIgnoreCase("MSH") && i < 3) {
                                escape = false;
                            }
                            List<HL7Exception> childExceptions = testField(s, field, escape, profileID);
                            for (HL7Exception ex : childExceptions) {
                                ex.setFieldPosition(i);
                            }
                            exList.addAll(childExceptions);
                        }
                    }

                } catch (HL7Exception he) {
                    exList.add(new ProfileNotHL7CompliantException("Field " + i
                            + " not found in message"));
                }
            }

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
    private List<HL7Exception> checkForExtraFields(Segment segment, List<Integer> allowedFields)
            throws ProfileException {
        ArrayList<HL7Exception> exList = new ArrayList<HL7Exception>();
        for (int i = 1; i <= segment.numFields(); i++) {
            if (!allowedFields.contains(new Integer(i))) {
                try {
                    Type[] reps = segment.getField(i);
                    for (Type rep : reps) {
                        if (!rep.isEmpty()) {
                            HL7Exception e = new XElementPresentException("Field " + i + " in "
                                    + segment.getName()
                                    + " appears in the message but not in the profile");
                            exList.add(e);
                        }
                    }
                } catch (HL7Exception he) {
                    throw new ProfileException("Problem testing against profile", he);
                }
            }
        }
        return exList;
    }

    public List<HL7Exception> testType(Type type, AbstractComponent<?> profile, String encoded, String profileID) {
        return testType(type, profile, encoded, profileID, true);
    }

    /**
     * Tests a Type against the corresponding section of a profile.
     *
     * @param encoded optional encoded form of type (if you want to specify this -- if null, default
     *            pipe-encoded form is used to check length and constant val)
     */
    public List<HL7Exception> testType(Type type, AbstractComponent<?> profile, String encoded,
                                       String profileID, boolean testUsage) {
        ArrayList<HL7Exception> exList = new ArrayList<HL7Exception>();
        if (encoded == null)
            encoded = PipeParser.encode(type, this.enc);

        if (testUsage){
            HL7Exception ue = testUsage(encoded, profile.getUsage(), profile.getName());
            if (ue != null)
                exList.add(ue);
        }

        if (!profile.getUsage().equals("X")) {
            // check datatype

            if ((type instanceof ca.uhn.hl7v2.model.v231.datatype.TSComponentOne
                || type instanceof ca.uhn.hl7v2.model.v24.datatype.TSComponentOne)
                && !profile.getDatatype().equals("ST")) {

                HL7Exception he = new HL7Exception(
                        "HL7 datatype ST does not match the one declared in the profile (" + profile.getDatatype() + ")");
                he.setError(ErrorCode.DATA_TYPE_ERROR);
                exList.add(he);
            } else if (!(type instanceof TSComponentOne)
                && type.getName().indexOf(profile.getDatatype()) < 0) {

                if (!(type.getClass().getSimpleName().equals("Varies") || type.getClass().getSimpleName().equals("QIP"))) {

                    HL7Exception he = new HL7Exception("HL7 datatype " + type.getName()
                            + " does not match the one declared in the profile (" + profile.getDatatype() + ")");
                    he.setError(ErrorCode.DATA_TYPE_ERROR);
                    exList.add(he);
                }
            }

            // check length
            if (encoded.length() > profile.getLength()){
                HL7Exception ex = new ProfileNotFollowedException("The type '" + profile.getName()
                        + "' has length " + encoded.length() + " which exceeds max of "
                        + profile.getLength());
                ex.setError(ErrorCode.DATA_TYPE_ERROR);
                ex.setSeverity(Severity.WARNING);
                exList.add(ex);
            }

            // check constant value
            if (profile.getConstantValue() != null && profile.getConstantValue().length() > 0) {
                if (!encoded.equals(profile.getConstantValue())){
                    HL7Exception he = new ProfileNotFollowedException("'" + encoded
                            + "' doesn't equal constant value of '" + profile.getConstantValue()
                            + "'");
                    he.setError(ErrorCode.DATA_TYPE_ERROR);
                    exList.add(he);
                }
            }

            exList.addAll(testTypeAgainstTable(type, profile, profileID));
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
    private HL7Exception testUsage(String encoded, String usage, String name) {
        HL7Exception e = null;
        if (usage.equalsIgnoreCase("R")) {
            if (encoded.length() == 0){
                e = new ProfileNotFollowedException("Required element '" + name + "' is missing");
            }
        } else if (usage.equalsIgnoreCase("RE")) {
            // can't test anything
        } else if (usage.equalsIgnoreCase("O")) {
            // can't test anything
        } else if (usage.equalsIgnoreCase("C")) {
            // can't test anything yet -- wait for condition syntax in v2.6
        } else if (usage.equalsIgnoreCase("CE")) {
            // can't test anything
        } else if (usage.equalsIgnoreCase("X")) {
            if (encoded.length() > 0){
                e = new XElementPresentException("Element \"" + name
                        + "\" is present but specified as not used (X)");
            }
        } else if (usage.equalsIgnoreCase("B")) {
            // can't test anything
        }
        return e;
    }

    /**
     * Tests table values for ID, IS, and CE types. An empty list is returned for all other types or
     * if the table name or number is missing.
     */
    private List<HL7Exception> testTypeAgainstTable(Type type, AbstractComponent<?> profile,
                                                    String profileID) {
        ArrayList<HL7Exception> exList = new ArrayList<HL7Exception>();
        if (profile.getTable() != null
                && (type.getName().equals("IS") || type.getName().equals("ID"))) {
            String codeSystem = makeTableName(profile.getTable());
            String value = ((Primitive) type).getValue();
            addTableTestResult(exList, profileID, codeSystem, value);
        } else if (type.getName().equals("CE")) {
            String value = Terser.getPrimitive(type, 1, 1).getValue();
            String codeSystem = Terser.getPrimitive(type, 3, 1).getValue();
            addTableTestResult(exList, profileID, codeSystem, value);

            value = Terser.getPrimitive(type, 4, 1).getValue();
            codeSystem = Terser.getPrimitive(type, 6, 1).getValue();
            addTableTestResult(exList, profileID, codeSystem, value);
        }
        return exList;
    }

    private void addTableTestResult(List<HL7Exception> exList, String profileID,
                                    String codeSystem, String value) {
        if (codeSystem != null && value != null) {
            HL7Exception e = testValueAgainstTable(profileID, codeSystem, value);
            if (e != null)
                exList.add(e);
        }
    }

    private HL7Exception testValueAgainstTable(String profileID, String codeSystem, String value) {
        HL7Exception ret = null;
        if (!validateChildren) {
            return ret;
        }

        CodeStore store = codeStore;
        if (codeStore == null) {
            store = getHapiContext().getCodeStoreRegistry().getCodeStore(profileID, codeSystem);
        }

        if (store == null) {
            log.warn(
                    "Not checking value {}: no code store was found for profile {} code system {}",
                    new Object[] { value, profileID, codeSystem });
        } else {
            if (!store.knowsCodes(codeSystem)) {
                log.warn("Not checking value {}: Don't have a table for code system {}", value,
                        codeSystem);
            } else if (!store.isValidCode(codeSystem, value)) {
                ret = new ProfileNotFollowedException("Code '" + value + "' not found in table "
                        + codeSystem + ", profile " + profileID);
            }
        }
        return ret;
    }

    private String makeTableName(String hl7Table) {
        return String.format("HL7%1$4s", hl7Table).replace(" ", "0");
    }

    private List<HL7Exception> testField(Type type, Field profile, boolean escape, String profileID) throws ProfileException {
        List<HL7Exception> exList = new ArrayList<HL7Exception>();

        // account for MSH 1 & 2 which aren't escaped
        String encoded = null;
        if (!escape && Primitive.class.isAssignableFrom(type.getClass()))
            encoded = ((Primitive) type).getValue();

        exList.addAll(testType(type, profile, encoded, profileID, false));

        // test children
        if (validateChildren) {
            if (profile.getComponents() > 0 && !profile.getUsage().equals("X")) {
                if (Composite.class.isAssignableFrom(type.getClass())) {
                    Composite comp = (Composite) type;
                    for (int i = 1; i <= profile.getComponents(); i++) {
                        Component childProfile = profile.getComponent(i);
                        try {
                            Type child = comp.getComponent(i - 1);
                            exList.addAll(testComponent(child, childProfile, profileID));
                        } catch (DataTypeException de) {
                            HL7Exception he = new ProfileNotHL7CompliantException("Component '" + type.getName()
                                    + "'[" + i + "] is defined in the profile but does not exist "
                                    + "in the Java class representing the segment");
                            he.setError(ErrorCode.DATA_TYPE_ERROR);
                            exList.add(he);
                        }
                    }
                    exList.addAll(checkExtraComponents(comp, profile.getComponents()));
                } else {
                    HL7Exception he = new ProfileNotHL7CompliantException("A field has type primitive "
                            + type.getClass().getName() + " but the profile defines components");
                    he.setError(ErrorCode.DATA_TYPE_ERROR);
                    he.setSeverity(Severity.INFO);
                    exList.add(he);
                }
            }
        }

        return exList;
    }

    private List<HL7Exception> testComponent(Type type, Component profile, String profileID) throws ProfileException {
        List<HL7Exception> exList = new ArrayList<HL7Exception>();
        exList.addAll(testType(type, profile, null, profileID));

        // test children
        if (profile.getSubComponents() > 0 && !profile.getUsage().equals("X") && hasContent(type)){
            if (Composite.class.isAssignableFrom(type.getClass())) {
                Composite comp = (Composite) type;

                if (validateChildren) {
                    for (int i = 1; i <= profile.getSubComponents(); i++) {
                        SubComponent childProfile = profile.getSubComponent(i);
                        try {
                            Type child = comp.getComponent(i - 1);
                            exList.addAll(testType(child, childProfile, null, profileID));
                        } catch (DataTypeException de) {
                            exList.add(new ProfileNotHL7CompliantException(
                                    "SubComponent '" + type.getName() + "'[" + i +
                                    "] is defined in the profile but does not exist in" +
                                    " the Java class representing the message"));
                        }
                    }
                }

                exList.addAll(checkExtraComponents(comp, profile.getSubComponents()));
            } else {
                exList.add(new ProfileNotFollowedException("A component has primitive type "
                        + type.getClass().getName() + " but the profile defines subcomponents"));
            }
        }

        return exList;
    }

    /** Tests for extra components (ie any not defined in the profile) */
    private List<HL7Exception> checkExtraComponents(Composite comp, int numInProfile)
            throws ProfileException {
        List<HL7Exception> exList = new ArrayList<HL7Exception>();

        StringBuffer extra = new StringBuffer();
        for (int i = numInProfile; i < comp.getComponents().length; i++) {
            try {
                String s = PipeParser.encode(comp.getComponent(i), enc);
                if (s.length() > 0) {
                    extra.append(s).append(enc.getComponentSeparator());
                }
            } catch (DataTypeException de) {
                throw new ProfileException("Problem testing against profile", de);
            }
        }

        if (extra.toString().length() > 0) {
            exList.add(new XElementPresentException(
                    "The following components are not defined in the profile: " + extra.toString()));
        }

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

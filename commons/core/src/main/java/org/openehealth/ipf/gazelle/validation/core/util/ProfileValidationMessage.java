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
package org.openehealth.ipf.gazelle.validation.core.util;

import ca.uhn.hl7v2.ErrorCode;
import ca.uhn.hl7v2.Severity;

import static ca.uhn.hl7v2.ErrorCode.APPLICATION_INTERNAL_ERROR;
import static ca.uhn.hl7v2.ErrorCode.DATA_TYPE_ERROR;
import static ca.uhn.hl7v2.ErrorCode.UNSUPPORTED_MESSAGE_TYPE;
import static ca.uhn.hl7v2.ErrorCode.UNSUPPORTED_EVENT_CODE;
import static ca.uhn.hl7v2.ErrorCode.UNSUPPORTED_PROCESSING_ID;
import static ca.uhn.hl7v2.ErrorCode.UNSUPPORTED_VERSION_ID;

import static ca.uhn.hl7v2.Severity.ERROR;
import static ca.uhn.hl7v2.Severity.WARNING;
import static ca.uhn.hl7v2.Severity.INFO;

/**
 * @author Boris Stanojevic
 */
public enum ProfileValidationMessage {

    WRONG_MSH_TYPE_FIELD("Message type '%1s' doesn't match profile type of '%2s'", UNSUPPORTED_MESSAGE_TYPE, ERROR),
    WRONG_MSH_EVENT_FIELD("Message event '%1s' doesn't match profile event of '%2s'", UNSUPPORTED_EVENT_CODE, ERROR),
    WRONG_MSH_STRUCTURE_FIELD("Message structure '%1s' doesn't match profile structure of '%2s'", UNSUPPORTED_PROCESSING_ID, ERROR),
    WRONG_MSH_VERSION_FIELD("Message version '%1s' doesn't match profile version of '%2s'", UNSUPPORTED_VERSION_ID, ERROR),
    NOT_SUPPORTED_ELEMENT_PRESENT("Element '%1s' is present in the message but specified as not used (X) by the profile", APPLICATION_INTERNAL_ERROR, WARNING),
    NO_ELEMENTS_AFTER_NULL("Element '%1s' cannot be present when the field is set to NULL", APPLICATION_INTERNAL_ERROR, ERROR),
    LENGTH_EXCEEDED("The type '%1s' has length %2s which exceeds max of %3s", DATA_TYPE_ERROR, WARNING),
    WRONG_CONSTANT_VALUE ("'%1s' doesn't equal constant value of '%2s'", DATA_TYPE_ERROR, ERROR),
    REQUIRED_ELEMENT_MISSING("Required element '%1s' is missing", APPLICATION_INTERNAL_ERROR, ERROR),
    WRONG_COMPONENT_TYPE("A component has primitive type %1s but the profile defines subcomponents", APPLICATION_INTERNAL_ERROR, ERROR),
    CODE_NOT_FOUND_IN_TABLE("Code '%1s' not found in table %2s, profile %3s",APPLICATION_INTERNAL_ERROR, ERROR),
    PROFILE_STRUCTURE_NOT_EXIST_IN_JAVA_CLASS("%1s is expected by the message profile but does not appear in the Java class representing the message structure", APPLICATION_INTERNAL_ERROR, INFO),
    PROFILE_STRUCTURE_MISMATCH("Mismatch between a %1s in the profile and the structure %2s in the message", APPLICATION_INTERNAL_ERROR, ERROR),
    FIELD_NOT_FOUND("Field %1s not found in message", APPLICATION_INTERNAL_ERROR, ERROR),
    WRONG_FIELD_TYPE("A field has type primitive '%1s' but the profile defines components", DATA_TYPE_ERROR, INFO),
    SUBCOMPONENT_TYPE_MISMATCH("SubComponent '%1s'[%2s] is defined in the profile but does not exist in the Java class representing the message", APPLICATION_INTERNAL_ERROR, ERROR),
    COMPONENT_TYPE_MISMATCH("Component '%1s'[2%s] is defined in the profile but does not exist in the Java class representing the segment", DATA_TYPE_ERROR, ERROR),
    HL7_DATATYPE_MISMATCH("HL7 datatype '%1s' does not match the one declared in the profile (%2s)", DATA_TYPE_ERROR, ERROR),
    COMPONENT_NOT_DEFINED_IN_PROFILE("The following components are not defined in the profile: '%1s'", APPLICATION_INTERNAL_ERROR, ERROR),
    STRUCTURE_NOT_DEFINED_IN_PROFILE("The structure '%1s' appears in the message but not in the profile", APPLICATION_INTERNAL_ERROR, ERROR),
    FIELD_NOT_DEFINED_IN_PROFILE("Field %1s in %2s appears in the message but not in the profile", APPLICATION_INTERNAL_ERROR, ERROR),
    LESS_THAN_MINIMUM_CARDINALITY("%1s must have at least %2s repetitions (has %3s)", APPLICATION_INTERNAL_ERROR, ERROR),
    MORE_THAN_MAXIMUM_CARDINALITY("%1s must have no more than %2s repetitions (has %3s)", APPLICATION_INTERNAL_ERROR, ERROR),;

    private String errorMessage;
    private ErrorCode errorCode;
    private Severity severity;

    private ProfileValidationMessage(String errorMessage, ErrorCode errorCode, Severity severity){
        this.errorMessage = errorMessage;
        this.errorCode = errorCode;
        this.severity = severity;
    }

    public String errorMessage() { return errorMessage; }
    public ErrorCode errorCode() { return errorCode; }
    public Severity severity()   { return severity; }
}

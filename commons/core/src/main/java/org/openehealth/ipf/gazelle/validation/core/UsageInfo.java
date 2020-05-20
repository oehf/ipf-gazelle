/*
 * Copyright 2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.openehealth.ipf.gazelle.validation.core;

import org.openehealth.ipf.gazelle.validation.core.stub.HL7V2XStaticDef;
import org.openehealth.ipf.gazelle.validation.core.stub.SegmentType;

/**
* Helper class that encapsulate usage-specific attributes of a confirmance profile
*/
class UsageInfo {
    String name;
    String usage;
    int max;
    int min;
    int length;
    String constantValue;

    UsageInfo(Object profileElement) {
        if (profileElement.getClass().isAssignableFrom(SegmentType.class)) {
            SegmentType structure = (SegmentType) profileElement;
            usage = structure.getUsage();
            name = structure.getName();
            max = structure.getMax().equals("*") ? Short.MAX_VALUE : Short.parseShort(structure.getMax());
            min = structure.getMin().intValue();
        } else if (profileElement.getClass().isAssignableFrom(HL7V2XStaticDef.SegGroup.class)) {
            HL7V2XStaticDef.SegGroup segGroup = (HL7V2XStaticDef.SegGroup) profileElement;
            usage = segGroup.getUsage();
            name = segGroup.getName();
            max = segGroup.getMax().equals("*") ? Short.MAX_VALUE : Short.parseShort(segGroup.getMax());
            min = segGroup.getMin().intValue();
        } else {
            // Should never happen as the XML schema and JAXB translation do not yield other types
            throw new IllegalArgumentException(profileElement.getClass().getName() + " is not supported as profile element.");
        }
    }

    UsageInfo(SegmentType.Field field) {
        usage = field.getUsage();
        name = field.getName();
        max = field.getMax().equals("*") ? Short.MAX_VALUE : Short.parseShort(field.getMax());
        min = field.getMin().intValue();
        length = field.getLength().intValue();
        constantValue = field.getConstantValue();
    }

    UsageInfo(SegmentType.Field.Component component) {
        usage = component.getUsage();
        name = component.getName();
        length = component.getLength() != null ? component.getLength().intValue() : 255;
        constantValue = component.getConstantValue();
    }

    UsageInfo(SegmentType.Field.Component.SubComponent subcomponent) {
        usage = subcomponent.getUsage();
        name = subcomponent.getName();
        length = subcomponent.getLength() != null ? subcomponent.getLength().intValue() : 255;
        constantValue = subcomponent.getConstantValue();
    }


    boolean disallowed() {
        return "X".equalsIgnoreCase(usage);
    }

    boolean required() {
        return "R".equalsIgnoreCase(usage);
    }

    boolean requiredOrEmpty() {
        return "RE".equalsIgnoreCase(usage);
    }

    boolean nullContext() {
        return "NULL".equalsIgnoreCase(usage);
    }
}

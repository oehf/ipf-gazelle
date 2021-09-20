# IPF-Gazelle

![Build Status](https://github.com/oehf/ipf-gazelle/actions/workflows/build.yml/badge.svg)

IPF-Gazelle produces artifacts containing all available Gazelle HL7v2 validation profiles. It is an upstream project
to [IPF](https://github.com/oehf/ipf), built and released independently.

The following extensions to the HL7v2 conformance profile syntax are supported:
* attribute /HL7v2xConformanceProfile/HL7v2xStaticDef/Segment/Field/@ipf:Nullable controls whether the HL7 NULL "" is a valid value of the whole field.

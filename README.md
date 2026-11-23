# IPF-Gazelle

![Build Status](https://github.com/oehf/ipf-gazelle/actions/workflows/build.yml/badge.svg)

IPF-Gazelle produces artifacts containing all available Gazelle HL7v2 validation profiles. It is an upstream project
to [IPF](https://github.com/oehf/ipf), built and released independently.

The following extensions to the HL7v2 conformance profile syntax are supported:
* attribute /HL7v2xConformanceProfile/HL7v2xStaticDef/Segment/Field/@ipf:Nullable controls whether the HL7 NULL "" is a valid value of the whole field.

## Known deviations between IHE technical frameworks and Gazelle profiles

The Gazelle profiles do not always agree with the IHE technical frameworks they are derived from. Where they disagree,
IPF-Gazelle follows the IHE technical framework. Where IHE and Gazelle agree, that value is used.

### HL7 version

The HL7 version declared by a `ConformanceProfile` constant may differ from the `HL7Version` attribute of the
Gazelle profile:

| Profile constant | IHE technical framework | Gazelle profile |
|---|---|---|
| `LAB_61_OML_O33`, `LAB_61_ORL_O34` | 2.5.1 ([PaLM TF-2c Rev. 11.0, 3.44.3](https://www.ihe.net/uploadedFiles/Documents/PaLM/IHE_PaLM_TF_Vol2c.pdf)) | 2.5 |
| `LAB_62_QBP_SLI`, `LAB_62_RSP_SLI` | 2.5.1 ([PaLM TF-2c Rev. 11.0, 3.45.3](https://www.ihe.net/uploadedFiles/Documents/PaLM/IHE_PaLM_TF_Vol2c.pdf)) | 2.5 |
| `LAB_63_OML_O33`, `LAB_63_ORL_O34` | 2.5.1 ([PaLM TF-2c Rev. 11.0, 3.46.3](https://www.ihe.net/uploadedFiles/Documents/PaLM/IHE_PaLM_TF_Vol2c.pdf)); the [LBL Supplement Rev. 1.2 (TI 2015)](https://www.ihe.net/uploadedFiles/Documents/Laboratory/IHE_LAB_Suppl_LBL_Rev1.2_TI_2015-07-14.pdf) still said 2.5 | 2.5 (but `Topics` says `confsig-IHE-2.5.1-...`) |
| `PAT_10_ORU_R01` | 2.5.1 ([ARPH Supplement Rev. 2.0 (TI 2010), Z.3](https://www.ihe.net/Technical_Framework/upload/IHE_PAT_Suppl_ARPH_Rev2-0_TI_2010-07-23.pdf)) | 2.5 |

To avoid rejecting messages because of this, versions x.y and x.y.z (e.g. 2.5 and 2.5.1) are treated as compatible:

* when guessing the profile for a message, a profile with the exact version is preferred, otherwise a compatible one is used;
* a compatible but different MSH-12 value is reported as `COMPATIBLE_MSH_VERSION_FIELD` warning instead of an error.

These cases are tracked in `AllProfilesTest.KNOWN_VERSION_MISMATCHES`. A fix in the upstream Gazelle profiles is
still desirable.

### Radiology ORM/ORR "V25" profiles

IHE RAD TF-2 defines ORM^O01 and ORR^O02 for HL7 v2.3.1 only; the v2.5.1 option uses OMG/ORG instead. Gazelle serves
byte-identical v2.3.1 profiles under the OIDs `1.3.6.1.4.12559.11.1.1.103`–`105` ("V25") and `...194`–`196` ("V231").
Consequently `RAD_2_ORM_O01_NW_V25`, `RAD_2_ORM_O01_CA_V25` and `RAD_3_ORR_O02_V25` declare version 2.3.1, are
deprecated and are no longer part of `RadTransactions`. Use the corresponding `_V231` constants instead.

### Data types

Some Gazelle profiles declare data types or component structures that differ from the HAPI model classes for the
profile's HL7 version. Validation then reports `HL7_DATATYPE_MISMATCH` (or `WRONG_FIELD_TYPE` /
`WRONG_COMPONENT_TYPE` when the profile defines components for a primitive HAPI type) as soon as the element is
populated. Elements declared as not supported (X) are exempt: their data type is not checked.

In all cases below, the HAPI model classes match the HL7 v2 standard. The "Defined by" column states whether the
Gazelle profile follows a deliberate deviation of the IHE technical framework from the HL7 standard (**IHE**), or
whether the IHE technical framework follows or does not constrain the HL7 standard, so that the Gazelle profile is
wrong (**Gazelle**).

#### Errors for valid messages

Messages that are correct according to the IHE technical framework fail validation in these cases:

| Profile constant(s) | HL7 | Element(s) | Gazelle profile vs. HL7/HAPI | Defined by | Source |
|---|---|---|---|---|---|
| `LAB_28_OML_O33`, `LAB_28_ORL_O34`, `LAB_29_OUL_R22` | 2.5.1 | OBX-4 (R) | OG vs. ST | IHE: data type pre-adopted from HL7 v2.8.2 | [PaLM TF-2b Rev. 11.0, C.7, Table C.7-1 and C.7-4](https://www.ihe.net/uploadedFiles/Documents/PaLM/IHE_PaLM_TF_Vol2b.pdf) |
| same | 2.5.1 | OBX-8 (R) | CWE vs. IS | IHE: pre-adopted from HL7 v2.7 | [PaLM TF-2b Rev. 11.0, C.7](https://www.ihe.net/uploadedFiles/Documents/PaLM/IHE_PaLM_TF_Vol2b.pdf) |
| same | 2.5.1 | ORC-4 (RE) | EIP vs. EI | IHE: pre-adopted from HL7 v2.8 | [PaLM TF-2b Rev. 11.0, C.8, Table C.8-4](https://www.ihe.net/uploadedFiles/Documents/PaLM/IHE_PaLM_TF_Vol2b.pdf) |
| same | 2.5.1 | PID-35 (RE) | CWE vs. CE | IHE: "Taxonomic Classification Code (CWE)", the HL7 v2.7 definition of PID-35 | [PaLM TF-2b Rev. 11.0, C.9, Table C.9-7](https://www.ihe.net/uploadedFiles/Documents/PaLM/IHE_PaLM_TF_Vol2b.pdf) |
| `PCD_1_ORU_R01_DEV_OBS_FILTER`, `PCD_1_ORU_R01_DEV_OBS_REPORTER` | 2.6 | PID-7, PID-29, PID-33, OBR-7, OBX-14 and all other timestamps | TS (with components) vs. DTM | Gazelle: IHE uses DTM | [DEV TF-2 Rev. 11.0, B.5 PID](https://www.ihe.net/uploadedFiles/Documents/DEV/IHE_DEV_TF_Vol2.pdf) and [C.4 DTM](https://www.ihe.net/uploadedFiles/Documents/DEV/IHE_DEV_TF_Vol2.pdf) |
| same | 2.6 | PV1-37.1 (R if PV1-37 is sent) | IS vs. CWE | Gazelle: IHE does not support PV1-37 (X) | [DEV TF-2 Rev. 11.0, B.6 PV1](https://www.ihe.net/uploadedFiles/Documents/DEV/IHE_DEV_TF_Vol2.pdf) |
| same | 2.6 | FT1-25, FT1-26, FT1-29 | CWE vs. CNE, CNE vs. CWE | Gazelle: IHE does not support FT1 (X) | [DEV TF-2 Rev. 11.0, 3.1.4.1.1](https://www.ihe.net/uploadedFiles/Documents/DEV/IHE_DEV_TF_Vol2.pdf) |
| same | 2.6 | OBR-44, OBR-45 | CWE vs. CNE | Gazelle: not constrained by IHE | [DEV TF-2 Rev. 11.0, B.7 OBR](https://www.ihe.net/uploadedFiles/Documents/DEV/IHE_DEV_TF_Vol2.pdf) |
| `PHARM_H6_RRE_O12` | 2.6 | XTN in PID-13, PID-14, ORC-14, ORC-23 | all components shifted by one, because the profile lists "Telephone Number" twice | Gazelle: IHE uses the standard XTN components | [Pharmacy HMW Supplement Rev. 1.4 (TI 2013), PID-13](https://www.ihe.net/uploadedFiles/Documents/Pharmacy/IHE_Pharmacy_Suppl_HMW.pdf) |

The LAW deviations (LAB-28/29) are intentional: IHE LAW is based on HL7 v2.5.1, but pre-adopts some data types from
later versions, which the HAPI v2.5.1 model classes cannot represent.

The Gazelle profiles for PCD-2 (`PCD_2_QSB_Z02`, `PCD_2_ACK_Z02`, HL7 2.5) declare MSH-7 as DTM instead of TS, so every
message fails validation. However, the IHE Devices Technical Framework no longer defines this transaction
([DEV TF-2 Rev. 11.0, 3.2: "[PCD-02] Reserved"](https://www.ihe.net/uploadedFiles/Documents/DEV/IHE_DEV_TF_Vol2.pdf)),
so these profiles and `PcdTransactions.PCD2` are deprecated.

The LAB-23 profiles (`LAB_23_ACK_R22`, `LAB_23_OUL_R22`) and `LabTransactions.LAB23` are deprecated as well: IHE PaLM TF
Rev. 7.0 (2016) moved LAB-23 "AWOS Status Change" from the LDA Profile to the LAW Profile as LAB-29
([PaLM TF-2a Rev. 11.0, revision history](https://www.ihe.net/uploadedFiles/Documents/PaLM/IHE_PaLM_TF_Vol2a.pdf);
[PaLM TF-1 Rev. 11.0, Table 4.1-1](https://www.ihe.net/uploadedFiles/Documents/PaLM/IHE_PaLM_TF_Vol1.pdf)).

#### Optional elements that are rarely populated

None of these elements is constrained by the respective IHE technical framework, so the HL7 standard applies and the
Gazelle profile is wrong in all cases:

| HL7 | Element(s) | Gazelle profile vs. HL7/HAPI | Affected profiles | IHE source (no constraint) |
|---|---|---|---|---|
| 2.3.1 | [XON.4](https://hl7-definition.caristix.com/v2/HL7v2.3.1/DataTypes/XON) check digit (PD1-3, PV2-23, NK1-13, ORC-21, GT1, IN1, IN2) | NM vs. ST | `ITI_8_*`, most RAD | [ITI TF-2, ITI-8](https://profiles.ihe.net/ITI/TF/Volume2/ITI-8.html), [RAD TF-2 Rev. 23.0](https://www.ihe.net/uploadedFiles/Documents/Radiology/IHE_RAD_TF_Vol2.pdf) |
| 2.3.1 | [DLD.1](https://hl7-definition.caristix.com/v2/HL7v2.3.1/DataTypes/DLD) discharge location (PV1-37.1) | ID vs. IS | `ITI_8_*`, most RAD | [ITI TF-2, ITI-8](https://profiles.ihe.net/ITI/TF/Volume2/ITI-8.html), [RAD TF-2 Rev. 23.0, Table 4.1-3](https://www.ihe.net/uploadedFiles/Documents/Radiology/IHE_RAD_TF_Vol2.pdf) (PV1-37 just "CM") |
| 2.3.1 | [PV2-16 to PV2-19](https://hl7-definition.caristix.com/v2/HL7v2.3.1/Segments/PV2) | shifted by one: the profile has PV2-15 "Employment Illness Related Indicator" at position 16 | RAD ORM, ORU, BAR | RAD TF-2 Rev. 23.0 (PV2 not mentioned) |
| 2.3.1 | IN3-5, IN3-11, IN3-20, UB1-10, UB1-16, UB2-6, UB2-7, UB2-8 | PEN vs. MOP, CM_DTN vs. DTN etc. (HL7 2.3.1 only calls these "CM") | `RAD_1_ADT_A04`, `RAD_35_DFT_P03` | RAD TF-2 Rev. 23.0 (IN3, UB1, UB2 not mentioned) |
| 2.3.1 | [XON.7](https://hl7-definition.caristix.com/v2/HL7v2.3.1/DataTypes/XON) identifier type code (IN1-9) | ID vs. IS | `RAD_3_ORM_O01_SN_RAD`, `RAD_3_ORM_O01_SN_EYE` | RAD TF-2 Rev. 23.0 |
| 2.4 | [XON.4](https://hl7-definition.caristix.com/v2/HL7v2.4/DataTypes/XON) check digit (PD1-3, PD1-14, PV2-23), [OSD.6](https://hl7-definition.caristix.com/v2/HL7v2.4/DataTypes/OSD) (SCH-11.10.6) | NM vs. ST, ID vs. ST | `RAD_48_SIU_*` | RAD TF-2 Rev. 23.0 |
| 2.5 | [NDL.1](https://hl7-definition.caristix.com/v2/HL7v2.5/DataTypes/NDL) (OBR-32 to OBR-35) | CNS vs. CNN | `CARD_7_*`, `CARD_8_*`, `PAT_10_ORU_R01` | [CARD DRPT Supplement (TI 2013)](https://www.ihe.net/uploadedFiles/Documents/Cardiology/IHE_CARD_Suppl_DRPT.pdf) |
| 2.5 | [XON.7](https://hl7-definition.caristix.com/v2/HL7v2.5/DataTypes/XON) identifier type code (SFT-1, ORC-21, PD1-3, PD1-14, PV2-23, NK1-13) | IS vs. ID | `CARD_7_*`, `CARD_8_*`, `PAT_10_ORU_R01` | [CARD DRPT Supplement (TI 2013)](https://www.ihe.net/uploadedFiles/Documents/Cardiology/IHE_CARD_Suppl_DRPT.pdf) |
| 2.5.1 | OBX-24 (XAD.13/14, DR), OBX-25 (XCN.16/19/20, DR) | DTM vs. TS, CWE vs. CE (HL7 v2.7 component types) | `LAB_28_*`, `LAB_29_OUL_R22` | [PaLM TF-2b Rev. 11.0, Table C.7-1](https://www.ihe.net/uploadedFiles/Documents/PaLM/IHE_PaLM_TF_Vol2b.pdf) (OBX-24/25 not listed) |
| 2.5.1 | [MSA-5](https://hl7-definition.caristix.com/v2/HL7v2.5.1/Segments/MSA) | ID vs. withdrawn field | `LAB_36_ACK` | [LAB ILW Supplement Rev. 1.2 (TI 2013)](https://www.ihe.net/uploadedFiles/Documents/Laboratory/IHE_LAB_Suppl_ILW.pdf) (MSA-5 not mentioned) |
| 2.6 | XTN.15, XTN.17, XTN.18 | shifted, see PHARM-H6 above | `PHARM_H6_RRE_O12` | see above |

#### Harmless

* HL7 2.5/2.5.1/2.6: TS.2 (degree of precision) is declared as ST instead of ID in about 180 profiles. It is always
  declared as not supported (X), so its data type is never checked.
* HL7 2.3.1/2.4: TS.1 is declared as ST; this is explicitly accepted by `GazelleProfileRule`.

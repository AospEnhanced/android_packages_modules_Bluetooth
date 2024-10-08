/******************************************************************************
 *
 *  Copyright 2000-2012 Broadcom Corporation
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at:
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 ******************************************************************************/

#pragma once

#include <bluetooth/log.h>

#include <cstdint>

enum tA2DP_STATUS : uint8_t {
  /* Success */
  A2DP_SUCCESS = 0,

  /* Failed */
  A2DP_FAIL = 0x0A,

  /* A2DP_FindService is already in progress */
  A2DP_BUSY = 0x0B,

  /* Bad parameters */
  A2DP_INVALID_PARAMS = 0x0C,

  /* Wrong codec info */
  A2DP_WRONG_CODEC = 0x0D,

  /* Media Codec Type is not valid */
  A2DP_BAD_CODEC_TYPE = 0xC1,

  /* Media Codec Type is not supported */
  A2DP_NS_CODEC_TYPE = 0xC2,

  /* Sampling Frequency is not valid or multiple values have been selected */
  A2DP_BAD_SAMP_FREQ = 0xC3,

  /* Sampling Frequency is not supported */
  A2DP_NS_SAMP_FREQ = 0xC4,

  /* Channel Mode is not valid or multiple values * have been selected */
  A2DP_BAD_CH_MODE = 0xC5,

  /* Channel Mode is not supported */
  A2DP_NS_CH_MODE = 0xC6,

  /* None or multiple values have been selected for Number of Subbands */
  A2DP_BAD_SUBBANDS = 0xC7,

  /* Number of Subbands is not supported */
  A2DP_NS_SUBBANDS = 0xC8,

  /* None or multiple values have been selected for Allocation Method */
  A2DP_BAD_ALLOC_METHOD = 0xC9,

  /* Allocation Method is not supported */
  A2DP_NS_ALLOC_METHOD = 0xCA,

  /* Minimum Bitpool Value is not valid */
  A2DP_BAD_MIN_BITPOOL = 0xCB,

  /* Minimum Bitpool Value is not supported */
  A2DP_NS_MIN_BITPOOL = 0xCC,

  /* Maximum Bitpool Value is not valid */
  A2DP_BAD_MAX_BITPOOL = 0xCD,

  /* Maximum Bitpool Value is not supported */
  A2DP_NS_MAX_BITPOOL = 0xCE,

  /* None or multiple values have been selected for Layer */
  A2DP_BAD_LAYER = 0xCF,

  /* Layer is not supported */
  A2DP_NS_LAYER = 0xD0,

  /* CRC is not supported */
  A2DP_NS_CRC = 0xD1,

  /* MPF-2 is not supported */
  A2DP_NS_MPF = 0xD2,

  /* VBR is not supported */
  A2DP_NS_VBR = 0xD3,

  /* None or multiple values have been selected for Bit Rate */
  A2DP_BAD_BIT_RATE = 0xD4,

  /* Bit Rate is not supported */
  A2DP_NS_BIT_RATE = 0xD5,

  /* Either 1) Object type is not valid (b3-b0) or 2) None or multiple values
   * have been * selected for Object Type
   */
  A2DP_BAD_OBJ_TYPE = 0xD6,

  /* Object type is not supported */
  A2DP_NS_OBJ_TYPE = 0xD7,

  /* None or multiple values have been selected for Channels */
  A2DP_BAD_CHANNEL = 0xD8,

  /* Channels is not supported */
  A2DP_NS_CHANNEL = 0xD9,

  /* None or multiple values have been selected for Block Length */
  A2DP_BAD_BLOCK_LEN = 0xDD,

  /* The requested CP Type is not supported. */
  A2DP_BAD_CP_TYPE = 0xE0,

  /* The format of Content Protection Service Capability/Content Protection
   * Scheme Dependent Data is not correct.
   */
  A2DP_BAD_CP_FORMAT = 0xE1,

  /* Invalid framesize */
  A2DP_NS_FRAMESIZE = 0xE2,
};

namespace fmt {
template <>
struct formatter<tA2DP_STATUS> : enum_formatter<tA2DP_STATUS> {};
}  // namespace fmt

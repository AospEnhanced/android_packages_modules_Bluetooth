/*
 * Copyright (C) 2014 The Android Open Source Project
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

package com.android.bluetooth.mapclient;

import android.annotation.Nullable;
import android.util.Log;

import com.android.internal.annotations.VisibleForTesting;

import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.DataInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.HashMap;

/**
 * Object representation of event report received by MNS
 *
 * <p>This object will be received in {@link Client#EVENT_EVENT_REPORT} callback message.
 */
public class EventReport {
    private static final String TAG = "EventReport";
    private final Type mType;
    private final String mDateTime;
    private final String mHandle;
    private final String mFolder;
    private final String mOldFolder;
    private final Bmessage.Type mMsgType;

    @VisibleForTesting
    EventReport(HashMap<String, String> attrs) throws IllegalArgumentException {
        mType = parseType(attrs.get("type"));

        if (mType != Type.MEMORY_FULL && mType != Type.MEMORY_AVAILABLE) {
            String handle = attrs.get("handle");
            try {
                /* just to validate */
                new BigInteger(attrs.get("handle"), 16);

                mHandle = attrs.get("handle");
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid value for handle:" + handle);
            }
        } else {
            mHandle = null;
        }

        mFolder = attrs.get("folder");

        mOldFolder = attrs.get("old_folder");

        mDateTime = attrs.get("datetime");

        if (mType != Type.MEMORY_FULL && mType != Type.MEMORY_AVAILABLE) {
            String s = attrs.get("msg_type");

            if (s != null && s.isEmpty()) {
                // Some phones (e.g. SGS3 for MessageDeleted) send empty
                // msg_type, in such case leave it as null rather than throw
                // parse exception
                mMsgType = null;
            } else {
                mMsgType = parseMsgType(s);
            }
        } else {
            mMsgType = null;
        }
    }

    static EventReport fromStream(DataInputStream in) {
        EventReport ev = null;

        try {
            XmlPullParser xpp = XmlPullParserFactory.newInstance().newPullParser();
            xpp.setInput(in, "utf-8");

            int event = xpp.getEventType();
            while (event != XmlPullParser.END_DOCUMENT) {
                switch (event) {
                    case XmlPullParser.START_TAG:
                        if (xpp.getName().equals("event")) {
                            HashMap<String, String> attrs = new HashMap<String, String>();

                            for (int i = 0; i < xpp.getAttributeCount(); i++) {
                                attrs.put(xpp.getAttributeName(i), xpp.getAttributeValue(i));
                            }

                            ev = new EventReport(attrs);

                            // return immediately, only one event should be here
                            return ev;
                        }
                        break;
                }

                event = xpp.next();
            }

        } catch (XmlPullParserException e) {
            Log.e(TAG, "XML parser error when parsing XML", e);
        } catch (IOException e) {
            Log.e(TAG, "I/O error when parsing XML", e);
        } catch (IllegalArgumentException e) {
            Log.e(TAG, "Invalid event received", e);
        }

        return ev;
    }

    private Type parseType(String type) throws IllegalArgumentException {
        for (Type t : Type.values()) {
            if (t.toString().equals(type)) {
                return t;
            }
        }

        throw new IllegalArgumentException("Invalid value for type: " + type);
    }

    private Bmessage.Type parseMsgType(String msgType) throws IllegalArgumentException {
        for (Bmessage.Type t : Bmessage.Type.values()) {
            if (t.name().equals(msgType)) {
                return t;
            }
        }

        throw new IllegalArgumentException("Invalid value for msg_type: " + msgType);
    }

    /**
     * @return {@link EventReport.Type} object corresponding to <code>type</code> application
     *     parameter in MAP specification
     */
    public Type getType() {
        return mType;
    }

    /**
     * @return value corresponding to <code>handle</code> parameter in MAP specification
     */
    public String getHandle() {
        return mHandle;
    }

    /**
     * @return value corresponding to <code>folder</code> parameter in MAP specification
     */
    public String getFolder() {
        return mFolder;
    }

    /**
     * @return value corresponding to <code>old_folder</code> parameter in MAP specification
     */
    public String getOldFolder() {
        return mOldFolder;
    }

    /**
     * @return {@link Bmessage.Type} object corresponding to <code>msg_type</code> application
     *     parameter in MAP specification
     */
    public Bmessage.Type getMsgType() {
        return mMsgType;
    }

    /**
     * @return value corresponding to <code>datetime</code> parameter in MAP specification for
     *     NEW_MESSAGE (can be null)
     */
    @Nullable
    public String getDateTime() {
        return mDateTime;
    }

    /**
     * @return timestamp from the value corresponding to <code>datetime</code> parameter in MAP
     *     specification for NEW_MESSAGE (can be null)
     */
    @Nullable
    public Long getTimestamp() {
        if (mDateTime != null) {
            ObexTime obexTime = new ObexTime(mDateTime);
            if (obexTime != null) {
                return obexTime.getInstant().toEpochMilli();
            }
        }
        return null;
    }

    @Override
    public String toString() {
        JSONObject json = new JSONObject();

        try {
            json.put("type", mType);
            if (mDateTime != null) {
                json.put("datetime", mDateTime);
            }
            json.put("handle", mHandle);
            json.put("folder", mFolder);
            json.put("old_folder", mOldFolder);
            json.put("msg_type", mMsgType);
        } catch (JSONException e) {
            // do nothing
        }

        return json.toString();
    }

    public enum Type {
        NEW_MESSAGE("NewMessage"),
        DELIVERY_SUCCESS("DeliverySuccess"),
        SENDING_SUCCESS("SendingSuccess"),
        DELIVERY_FAILURE("DeliveryFailure"),
        SENDING_FAILURE("SendingFailure"),
        MEMORY_FULL("MemoryFull"),
        MEMORY_AVAILABLE("MemoryAvailable"),
        MESSAGE_DELETED("MessageDeleted"),
        MESSAGE_SHIFT("MessageShift"),
        READ_STATUS_CHANGED("ReadStatusChanged"),
        MESSAGE_REMOVED("MessageRemoved"),
        MESSAGE_EXTENDED_DATA_CHANGED("MessageExtendedDataChanged"),
        PARTICIPANT_PRESENCE_CHANGED("ParticipantPresenceChanged"),
        PARTICIPANT_CHAT_STATE_CHANGED("ParticipantChatStateChanged"),
        CONVERSATION_CHANGED("ConversationChanged");
        private final String mSpecName;

        Type(String specName) {
            mSpecName = specName;
        }

        @Override
        public String toString() {
            return mSpecName;
        }
    }
}

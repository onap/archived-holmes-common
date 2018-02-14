/**
 * Copyright 2017 ZTE Corporation.
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

package org.onap.holmes.common.api.stat;

import com.alibaba.fastjson.JSONObject;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.*;


@Getter
@Setter
public class Alarm implements AplusData, Cloneable, Serializable {

    public static final byte EVENT_CLEARED_ALARM = 3;

    public static final byte EVENT_CHANGED = 2;

    public static final byte EVENT_CLEARED = 1;

    public static final byte EVENT_RAISED = 0;

    private static final long serialVersionUID = 4520003737132012000L;
    private final static Date clearedServerTime = null;
    private final Map<Integer, Integer> linkIdNodeIdxMap = new HashMap<Integer, Integer>();
    private byte eventType = EVENT_RAISED;
    private long id = 0L;
    private String alarmKey = "";
    private String network = "";
    private String neType = "";
    private String equipType = "";
    private String position1 = "";
    private String subPosition1 = null;
    private String position2 = null;
    private String subPosition2 = null;
    private byte severity = -1;
    private byte alarmType = -1;
    private long probableCause = -1;
    private String specificProblem = null;
    private String additionalText = null;
    private Date raisedTime = new Date();
    private Date raisedServerTime = new Date();
    private Date clearedTime = null;
    private String region = null;
    private String site = null;
    private String aid = null;
    private short systemType = -1;
    private boolean rootAlarmFlag = false;
    private int linkId = -1;
    private int nodeIdx = -1;
    private Set<Integer> linkIds = new HashSet<Integer>();
    private HashMap<String, Integer> priorityMap = new HashMap<String, Integer>();
    private HashMap<String, Integer> rootAlarmTypeMap = new HashMap<String, Integer>();
    private int rootAlarmType = -1;
    private boolean keyAlarmFlag = false;
    private int keyAlarmType = -1;
    private int networkLevel = -1;
    private int linkType = -1;
    private int centerType;

    public void addLinkIdNodeIdx(int linkId, int index) {
        linkIdNodeIdxMap.put(linkId, index);
    }

    /**
     * Comparison of the location of different links: 999 the same link: the location of the node
     * number less than otherIdIdx in the location of the number (in the otherIdIdx upstream):-1,
     * the same link: the node position number is equal to the number of position in the otherIdIdx
     * (in the same site otherIdIdx):0, the same link: the node location number is greater than the
     * number of locations in otherIdIdx (otherIdIdx) with the site):1,
     */
    public int CompareLinkPosition(Map<Integer, Integer> otherIdIdx) {
        Set<Integer> myIdSet = new HashSet<Integer>();
        myIdSet.addAll(this.linkIdNodeIdxMap.keySet());
        myIdSet.retainAll(otherIdIdx.keySet());

        if (myIdSet.isEmpty()) {
            return 999;
        }

        for (int tempLinkId : myIdSet) {
            return this.linkIdNodeIdxMap.get(tempLinkId) - otherIdIdx.get(tempLinkId);
        }
        return 999;
    }

    public boolean containNode(int linkId, int index) {
       return linkIdNodeIdxMap.containsKey(linkId) && linkIdNodeIdxMap.get(linkId) == index;
    }

    @Override
    public byte getDataType() {
        return APLUS_EVENT;
    }

    @Override
    public String toString() {

        return JSONObject.toJSONString(this);
    }

    @Override
    public int hashCode() {
        return this.getAlarmKey().hashCode();
    }

    @Override
    public boolean equals(Object arg0) {
        if (arg0 == null || !(arg0 instanceof Alarm)) {
            return false;
        }
        return this.alarmKey.equals(((Alarm) arg0).getAlarmKey());
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        super.clone();

        Alarm alarm = new Alarm();

        alarm.setEventType(this.getEventType());

        alarm.setAid(this.getAid());
        alarm.setId(this.getId());
        alarm.setAlarmKey(this.getAlarmKey());

        alarm.setNetwork(this.getNetwork());
        alarm.setEquipType(this.getEquipType());
        alarm.setNeType(this.getNeType());
        alarm.setPosition1(this.getPosition1());
        alarm.setSubPosition1(this.getSubPosition1());
        alarm.setPosition2(this.getPosition2());
        alarm.setSubPosition2(this.getSubPosition2());
        alarm.setRegion(this.getRegion());
        alarm.setSite(this.getSite());

        alarm.setSeverity(this.getSeverity());
        alarm.setAlarmType(this.getAlarmType());
        alarm.setSystemType(this.getSystemType());
        alarm.setSpecificProblem(this.getSpecificProblem());
        alarm.setAdditionalText(this.getAdditionalText());
        alarm.setProbableCause(this.getProbableCause());

        alarm.setRaisedTime(this.getRaisedTime());
        alarm.setRaisedServerTime(this.getRaisedServerTime());
        alarm.setClearedTime(this.getClearedTime());

        return alarm;
    }

    @Override
    public String getObjectId() {
        return String.valueOf(id);
    }

    public void addLinkIds(int linkId) {
        linkIds.add(linkId);
    }


    public boolean containsPriority(String ruleId) {
        if (priorityMap.keySet().contains(ruleId)) {
            return true;
        }
        return false;
    }

    public int getPriority(String ruleId) {
        Integer priority = this.priorityMap.get(ruleId);
        if (priority == null) {
            priority = 0;
        }
        return priority;
    }

    public int getRootAlarmType(String ruleId) {
        Integer rootAlarmType = this.rootAlarmTypeMap.get(ruleId);
        if (rootAlarmType == null) {
            rootAlarmType = -1;
        }
        return rootAlarmType;
    }

}

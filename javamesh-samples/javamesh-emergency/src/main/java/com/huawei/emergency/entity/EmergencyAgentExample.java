/*
 * Copyright (C) 2021-2022 Huawei Technologies Co., Ltd. All rights reserved.
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

package com.huawei.emergency.entity;

import java.util.ArrayList;
import java.util.List;

/**
 * emergency_agent query
 *
 * @author yds
 * @since 2022-05-23
 */
public class EmergencyAgentExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public EmergencyAgentExample() {
        oredCriteria = new ArrayList<>();
    }

    public void setOrderByClause(String orderByClause) {
        this.orderByClause = orderByClause;
    }

    public String getOrderByClause() {
        return orderByClause;
    }

    public void setDistinct(boolean distinct) {
        this.distinct = distinct;
    }

    public boolean isDistinct() {
        return distinct;
    }

    public List<Criteria> getOredCriteria() {
        return oredCriteria;
    }

    public void or(Criteria criteria) {
        oredCriteria.add(criteria);
    }

    public Criteria or() {
        Criteria criteria = createCriteriaInternal();
        oredCriteria.add(criteria);
        return criteria;
    }

    public Criteria createCriteria() {
        Criteria criteria = createCriteriaInternal();
        if (oredCriteria.size() == 0) {
            oredCriteria.add(criteria);
        }
        return criteria;
    }

    protected Criteria createCriteriaInternal() {
        Criteria criteria = new Criteria();
        return criteria;
    }

    public void clear() {
        oredCriteria.clear();
        orderByClause = null;
        distinct = false;
    }

    protected abstract static class GeneratedCriteria {
        protected List<Criterion> criteria;

        protected GeneratedCriteria() {
            super();
            criteria = new ArrayList<>();
        }

        public boolean isValid() {
            return criteria.size() > 0;
        }

        public List<Criterion> getAllCriteria() {
            return criteria;
        }

        public List<Criterion> getCriteria() {
            return criteria;
        }

        protected void addCriterion(String condition) {
            if (condition == null) {
                throw new RuntimeException("Value for condition cannot be null");
            }
            criteria.add(new Criterion(condition));
        }

        protected void addCriterion(String condition, Object value, String property) {
            if (value == null) {
                throw new RuntimeException("Value for " + property + " cannot be null");
            }
            criteria.add(new Criterion(condition, value));
        }

        protected void addCriterion(String condition, Object value1, Object value2, String property) {
            if (value1 == null || value2 == null) {
                throw new RuntimeException("Between values for " + property + " cannot be null");
            }
            criteria.add(new Criterion(condition, value1, value2));
        }

        public Criteria andAgentIdIsNull() {
            addCriterion("agent_id is null");
            return (Criteria) this;
        }

        public Criteria andAgentIdIsNotNull() {
            addCriterion("agent_id is not null");
            return (Criteria) this;
        }

        public Criteria andAgentIdEqualTo(Integer value) {
            addCriterion("agent_id =", value, "agentId");
            return (Criteria) this;
        }

        public Criteria andAgentIdNotEqualTo(Integer value) {
            addCriterion("agent_id <>", value, "agentId");
            return (Criteria) this;
        }

        public Criteria andAgentIdGreaterThan(Integer value) {
            addCriterion("agent_id >", value, "agentId");
            return (Criteria) this;
        }

        public Criteria andAgentIdGreaterThanOrEqualTo(Integer value) {
            addCriterion("agent_id >=", value, "agentId");
            return (Criteria) this;
        }

        public Criteria andAgentIdLessThan(Integer value) {
            addCriterion("agent_id <", value, "agentId");
            return (Criteria) this;
        }

        public Criteria andAgentIdLessThanOrEqualTo(Integer value) {
            addCriterion("agent_id <=", value, "agentId");
            return (Criteria) this;
        }

        public Criteria andAgentIdIn(List<Integer> values) {
            addCriterion("agent_id in", values, "agentId");
            return (Criteria) this;
        }

        public Criteria andAgentIdNotIn(List<Integer> values) {
            addCriterion("agent_id not in", values, "agentId");
            return (Criteria) this;
        }

        public Criteria andAgentIdBetween(Integer value1, Integer value2) {
            addCriterion("agent_id between", value1, value2, "agentId");
            return (Criteria) this;
        }

        public Criteria andAgentIdNotBetween(Integer value1, Integer value2) {
            addCriterion("agent_id not between", value1, value2, "agentId");
            return (Criteria) this;
        }

        public Criteria andAgentNameIsNull() {
            addCriterion("agent_name is null");
            return (Criteria) this;
        }

        public Criteria andAgentNameIsNotNull() {
            addCriterion("agent_name is not null");
            return (Criteria) this;
        }

        public Criteria andAgentNameEqualTo(String value) {
            addCriterion("agent_name =", value, "agentName");
            return (Criteria) this;
        }

        public Criteria andAgentNameNotEqualTo(String value) {
            addCriterion("agent_name <>", value, "agentName");
            return (Criteria) this;
        }

        public Criteria andAgentNameGreaterThan(String value) {
            addCriterion("agent_name >", value, "agentName");
            return (Criteria) this;
        }

        public Criteria andAgentNameGreaterThanOrEqualTo(String value) {
            addCriterion("agent_name >=", value, "agentName");
            return (Criteria) this;
        }

        public Criteria andAgentNameLessThan(String value) {
            addCriterion("agent_name <", value, "agentName");
            return (Criteria) this;
        }

        public Criteria andAgentNameLessThanOrEqualTo(String value) {
            addCriterion("agent_name <=", value, "agentName");
            return (Criteria) this;
        }

        public Criteria andAgentNameLike(String value) {
            addCriterion("agent_name like", value, "agentName");
            return (Criteria) this;
        }

        public Criteria andAgentNameNotLike(String value) {
            addCriterion("agent_name not like", value, "agentName");
            return (Criteria) this;
        }

        public Criteria andAgentNameIn(List<String> values) {
            addCriterion("agent_name in", values, "agentName");
            return (Criteria) this;
        }

        public Criteria andAgentNameNotIn(List<String> values) {
            addCriterion("agent_name not in", values, "agentName");
            return (Criteria) this;
        }

        public Criteria andAgentNameBetween(String value1, String value2) {
            addCriterion("agent_name between", value1, value2, "agentName");
            return (Criteria) this;
        }

        public Criteria andAgentNameNotBetween(String value1, String value2) {
            addCriterion("agent_name not between", value1, value2, "agentName");
            return (Criteria) this;
        }

        public Criteria andAgentIpIsNull() {
            addCriterion("agent_ip is null");
            return (Criteria) this;
        }

        public Criteria andAgentIpIsNotNull() {
            addCriterion("agent_ip is not null");
            return (Criteria) this;
        }

        public Criteria andAgentIpEqualTo(String value) {
            addCriterion("agent_ip =", value, "agentIp");
            return (Criteria) this;
        }

        public Criteria andAgentIpNotEqualTo(String value) {
            addCriterion("agent_ip <>", value, "agentIp");
            return (Criteria) this;
        }

        public Criteria andAgentIpGreaterThan(String value) {
            addCriterion("agent_ip >", value, "agentIp");
            return (Criteria) this;
        }

        public Criteria andAgentIpGreaterThanOrEqualTo(String value) {
            addCriterion("agent_ip >=", value, "agentIp");
            return (Criteria) this;
        }

        public Criteria andAgentIpLessThan(String value) {
            addCriterion("agent_ip <", value, "agentIp");
            return (Criteria) this;
        }

        public Criteria andAgentIpLessThanOrEqualTo(String value) {
            addCriterion("agent_ip <=", value, "agentIp");
            return (Criteria) this;
        }

        public Criteria andAgentIpLike(String value) {
            addCriterion("agent_ip like", value, "agentIp");
            return (Criteria) this;
        }

        public Criteria andAgentIpNotLike(String value) {
            addCriterion("agent_ip not like", value, "agentIp");
            return (Criteria) this;
        }

        public Criteria andAgentIpIn(List<String> values) {
            addCriterion("agent_ip in", values, "agentIp");
            return (Criteria) this;
        }

        public Criteria andAgentIpNotIn(List<String> values) {
            addCriterion("agent_ip not in", values, "agentIp");
            return (Criteria) this;
        }

        public Criteria andAgentIpBetween(String value1, String value2) {
            addCriterion("agent_ip between", value1, value2, "agentIp");
            return (Criteria) this;
        }

        public Criteria andAgentIpNotBetween(String value1, String value2) {
            addCriterion("agent_ip not between", value1, value2, "agentIp");
            return (Criteria) this;
        }

        public Criteria andAgentPortIsNull() {
            addCriterion("agent_port is null");
            return (Criteria) this;
        }

        public Criteria andAgentPortIsNotNull() {
            addCriterion("agent_port is not null");
            return (Criteria) this;
        }

        public Criteria andAgentPortEqualTo(Integer value) {
            addCriterion("agent_port =", value, "agentPort");
            return (Criteria) this;
        }

        public Criteria andAgentPortNotEqualTo(Integer value) {
            addCriterion("agent_port <>", value, "agentPort");
            return (Criteria) this;
        }

        public Criteria andAgentPortGreaterThan(Integer value) {
            addCriterion("agent_port >", value, "agentPort");
            return (Criteria) this;
        }

        public Criteria andAgentPortGreaterThanOrEqualTo(Integer value) {
            addCriterion("agent_port >=", value, "agentPort");
            return (Criteria) this;
        }

        public Criteria andAgentPortLessThan(Integer value) {
            addCriterion("agent_port <", value, "agentPort");
            return (Criteria) this;
        }

        public Criteria andAgentPortLessThanOrEqualTo(Integer value) {
            addCriterion("agent_port <=", value, "agentPort");
            return (Criteria) this;
        }

        public Criteria andAgentPortIn(List<Integer> values) {
            addCriterion("agent_port in", values, "agentPort");
            return (Criteria) this;
        }

        public Criteria andAgentPortNotIn(List<Integer> values) {
            addCriterion("agent_port not in", values, "agentPort");
            return (Criteria) this;
        }

        public Criteria andAgentPortBetween(Integer value1, Integer value2) {
            addCriterion("agent_port between", value1, value2, "agentPort");
            return (Criteria) this;
        }

        public Criteria andAgentPortNotBetween(Integer value1, Integer value2) {
            addCriterion("agent_port not between", value1, value2, "agentPort");
            return (Criteria) this;
        }

        public Criteria andAgentStatusIsNull() {
            addCriterion("agent_status is null");
            return (Criteria) this;
        }

        public Criteria andAgentStatusIsNotNull() {
            addCriterion("agent_status is not null");
            return (Criteria) this;
        }

        public Criteria andAgentStatusEqualTo(String value) {
            addCriterion("agent_status =", value, "agentStatus");
            return (Criteria) this;
        }

        public Criteria andAgentStatusNotEqualTo(String value) {
            addCriterion("agent_status <>", value, "agentStatus");
            return (Criteria) this;
        }

        public Criteria andAgentStatusGreaterThan(String value) {
            addCriterion("agent_status >", value, "agentStatus");
            return (Criteria) this;
        }

        public Criteria andAgentStatusGreaterThanOrEqualTo(String value) {
            addCriterion("agent_status >=", value, "agentStatus");
            return (Criteria) this;
        }

        public Criteria andAgentStatusLessThan(String value) {
            addCriterion("agent_status <", value, "agentStatus");
            return (Criteria) this;
        }

        public Criteria andAgentStatusLessThanOrEqualTo(String value) {
            addCriterion("agent_status <=", value, "agentStatus");
            return (Criteria) this;
        }

        public Criteria andAgentStatusLike(String value) {
            addCriterion("agent_status like", value, "agentStatus");
            return (Criteria) this;
        }

        public Criteria andAgentStatusNotLike(String value) {
            addCriterion("agent_status not like", value, "agentStatus");
            return (Criteria) this;
        }

        public Criteria andAgentStatusIn(List<String> values) {
            addCriterion("agent_status in", values, "agentStatus");
            return (Criteria) this;
        }

        public Criteria andAgentStatusNotIn(List<String> values) {
            addCriterion("agent_status not in", values, "agentStatus");
            return (Criteria) this;
        }

        public Criteria andAgentStatusBetween(String value1, String value2) {
            addCriterion("agent_status between", value1, value2, "agentStatus");
            return (Criteria) this;
        }

        public Criteria andAgentStatusNotBetween(String value1, String value2) {
            addCriterion("agent_status not between", value1, value2, "agentStatus");
            return (Criteria) this;
        }

        public Criteria andIsValidIsNull() {
            addCriterion("is_valid is null");
            return (Criteria) this;
        }

        public Criteria andIsValidIsNotNull() {
            addCriterion("is_valid is not null");
            return (Criteria) this;
        }

        public Criteria andIsValidEqualTo(String value) {
            addCriterion("is_valid =", value, "isValid");
            return (Criteria) this;
        }

        public Criteria andIsValidNotEqualTo(String value) {
            addCriterion("is_valid <>", value, "isValid");
            return (Criteria) this;
        }

        public Criteria andIsValidGreaterThan(String value) {
            addCriterion("is_valid >", value, "isValid");
            return (Criteria) this;
        }

        public Criteria andIsValidGreaterThanOrEqualTo(String value) {
            addCriterion("is_valid >=", value, "isValid");
            return (Criteria) this;
        }

        public Criteria andIsValidLessThan(String value) {
            addCriterion("is_valid <", value, "isValid");
            return (Criteria) this;
        }

        public Criteria andIsValidLessThanOrEqualTo(String value) {
            addCriterion("is_valid <=", value, "isValid");
            return (Criteria) this;
        }

        public Criteria andIsValidLike(String value) {
            addCriterion("is_valid like", value, "isValid");
            return (Criteria) this;
        }

        public Criteria andIsValidNotLike(String value) {
            addCriterion("is_valid not like", value, "isValid");
            return (Criteria) this;
        }

        public Criteria andIsValidIn(List<String> values) {
            addCriterion("is_valid in", values, "isValid");
            return (Criteria) this;
        }

        public Criteria andIsValidNotIn(List<String> values) {
            addCriterion("is_valid not in", values, "isValid");
            return (Criteria) this;
        }

        public Criteria andIsValidBetween(String value1, String value2) {
            addCriterion("is_valid between", value1, value2, "isValid");
            return (Criteria) this;
        }

        public Criteria andIsValidNotBetween(String value1, String value2) {
            addCriterion("is_valid not between", value1, value2, "isValid");
            return (Criteria) this;
        }
    }

    public static class Criteria extends GeneratedCriteria {
        protected Criteria() {
            super();
        }
    }

    public static class Criterion {
        private String condition;

        private Object value;

        private Object secondValue;

        private boolean noValue;

        private boolean singleValue;

        private boolean betweenValue;

        private boolean listValue;

        private String typeHandler;

        public String getCondition() {
            return condition;
        }

        public Object getValue() {
            return value;
        }

        public Object getSecondValue() {
            return secondValue;
        }

        public boolean isNoValue() {
            return noValue;
        }

        public boolean isSingleValue() {
            return singleValue;
        }

        public boolean isBetweenValue() {
            return betweenValue;
        }

        public boolean isListValue() {
            return listValue;
        }

        public String getTypeHandler() {
            return typeHandler;
        }

        protected Criterion(String condition) {
            super();
            this.condition = condition;
            this.typeHandler = null;
            this.noValue = true;
        }

        protected Criterion(String condition, Object value, String typeHandler) {
            super();
            this.condition = condition;
            this.value = value;
            this.typeHandler = typeHandler;
            if (value instanceof List<?>) {
                this.listValue = true;
            } else {
                this.singleValue = true;
            }
        }

        protected Criterion(String condition, Object value) {
            this(condition, value, null);
        }

        protected Criterion(String condition, Object value, Object secondValue, String typeHandler) {
            super();
            this.condition = condition;
            this.value = value;
            this.secondValue = secondValue;
            this.typeHandler = typeHandler;
            this.betweenValue = true;
        }

        protected Criterion(String condition, Object value, Object secondValue) {
            this(condition, value, secondValue, null);
        }
    }
}
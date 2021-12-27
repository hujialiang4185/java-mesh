package com.huawei.emergency.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class EmergencyElementExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public EmergencyElementExample() {
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

        public Criteria andElementIdIsNull() {
            addCriterion("element_id is null");
            return (Criteria) this;
        }

        public Criteria andElementIdIsNotNull() {
            addCriterion("element_id is not null");
            return (Criteria) this;
        }

        public Criteria andElementIdEqualTo(Integer value) {
            addCriterion("element_id =", value, "elementId");
            return (Criteria) this;
        }

        public Criteria andElementIdNotEqualTo(Integer value) {
            addCriterion("element_id <>", value, "elementId");
            return (Criteria) this;
        }

        public Criteria andElementIdGreaterThan(Integer value) {
            addCriterion("element_id >", value, "elementId");
            return (Criteria) this;
        }

        public Criteria andElementIdGreaterThanOrEqualTo(Integer value) {
            addCriterion("element_id >=", value, "elementId");
            return (Criteria) this;
        }

        public Criteria andElementIdLessThan(Integer value) {
            addCriterion("element_id <", value, "elementId");
            return (Criteria) this;
        }

        public Criteria andElementIdLessThanOrEqualTo(Integer value) {
            addCriterion("element_id <=", value, "elementId");
            return (Criteria) this;
        }

        public Criteria andElementIdIn(List<Integer> values) {
            addCriterion("element_id in", values, "elementId");
            return (Criteria) this;
        }

        public Criteria andElementIdNotIn(List<Integer> values) {
            addCriterion("element_id not in", values, "elementId");
            return (Criteria) this;
        }

        public Criteria andElementIdBetween(Integer value1, Integer value2) {
            addCriterion("element_id between", value1, value2, "elementId");
            return (Criteria) this;
        }

        public Criteria andElementIdNotBetween(Integer value1, Integer value2) {
            addCriterion("element_id not between", value1, value2, "elementId");
            return (Criteria) this;
        }

        public Criteria andElementNoIsNull() {
            addCriterion("element_no is null");
            return (Criteria) this;
        }

        public Criteria andElementNoIsNotNull() {
            addCriterion("element_no is not null");
            return (Criteria) this;
        }

        public Criteria andElementNoEqualTo(String value) {
            addCriterion("element_no =", value, "elementNo");
            return (Criteria) this;
        }

        public Criteria andElementNoNotEqualTo(String value) {
            addCriterion("element_no <>", value, "elementNo");
            return (Criteria) this;
        }

        public Criteria andElementNoGreaterThan(String value) {
            addCriterion("element_no >", value, "elementNo");
            return (Criteria) this;
        }

        public Criteria andElementNoGreaterThanOrEqualTo(String value) {
            addCriterion("element_no >=", value, "elementNo");
            return (Criteria) this;
        }

        public Criteria andElementNoLessThan(String value) {
            addCriterion("element_no <", value, "elementNo");
            return (Criteria) this;
        }

        public Criteria andElementNoLessThanOrEqualTo(String value) {
            addCriterion("element_no <=", value, "elementNo");
            return (Criteria) this;
        }

        public Criteria andElementNoLike(String value) {
            addCriterion("element_no like", value, "elementNo");
            return (Criteria) this;
        }

        public Criteria andElementNoNotLike(String value) {
            addCriterion("element_no not like", value, "elementNo");
            return (Criteria) this;
        }

        public Criteria andElementNoIn(List<String> values) {
            addCriterion("element_no in", values, "elementNo");
            return (Criteria) this;
        }

        public Criteria andElementNoNotIn(List<String> values) {
            addCriterion("element_no not in", values, "elementNo");
            return (Criteria) this;
        }

        public Criteria andElementNoBetween(String value1, String value2) {
            addCriterion("element_no between", value1, value2, "elementNo");
            return (Criteria) this;
        }

        public Criteria andElementNoNotBetween(String value1, String value2) {
            addCriterion("element_no not between", value1, value2, "elementNo");
            return (Criteria) this;
        }

        public Criteria andElementTitleIsNull() {
            addCriterion("element_title is null");
            return (Criteria) this;
        }

        public Criteria andElementTitleIsNotNull() {
            addCriterion("element_title is not null");
            return (Criteria) this;
        }

        public Criteria andElementTitleEqualTo(String value) {
            addCriterion("element_title =", value, "elementTitle");
            return (Criteria) this;
        }

        public Criteria andElementTitleNotEqualTo(String value) {
            addCriterion("element_title <>", value, "elementTitle");
            return (Criteria) this;
        }

        public Criteria andElementTitleGreaterThan(String value) {
            addCriterion("element_title >", value, "elementTitle");
            return (Criteria) this;
        }

        public Criteria andElementTitleGreaterThanOrEqualTo(String value) {
            addCriterion("element_title >=", value, "elementTitle");
            return (Criteria) this;
        }

        public Criteria andElementTitleLessThan(String value) {
            addCriterion("element_title <", value, "elementTitle");
            return (Criteria) this;
        }

        public Criteria andElementTitleLessThanOrEqualTo(String value) {
            addCriterion("element_title <=", value, "elementTitle");
            return (Criteria) this;
        }

        public Criteria andElementTitleLike(String value) {
            addCriterion("element_title like", value, "elementTitle");
            return (Criteria) this;
        }

        public Criteria andElementTitleNotLike(String value) {
            addCriterion("element_title not like", value, "elementTitle");
            return (Criteria) this;
        }

        public Criteria andElementTitleIn(List<String> values) {
            addCriterion("element_title in", values, "elementTitle");
            return (Criteria) this;
        }

        public Criteria andElementTitleNotIn(List<String> values) {
            addCriterion("element_title not in", values, "elementTitle");
            return (Criteria) this;
        }

        public Criteria andElementTitleBetween(String value1, String value2) {
            addCriterion("element_title between", value1, value2, "elementTitle");
            return (Criteria) this;
        }

        public Criteria andElementTitleNotBetween(String value1, String value2) {
            addCriterion("element_title not between", value1, value2, "elementTitle");
            return (Criteria) this;
        }

        public Criteria andElementTypeIsNull() {
            addCriterion("element_type is null");
            return (Criteria) this;
        }

        public Criteria andElementTypeIsNotNull() {
            addCriterion("element_type is not null");
            return (Criteria) this;
        }

        public Criteria andElementTypeEqualTo(String value) {
            addCriterion("element_type =", value, "elementType");
            return (Criteria) this;
        }

        public Criteria andElementTypeNotEqualTo(String value) {
            addCriterion("element_type <>", value, "elementType");
            return (Criteria) this;
        }

        public Criteria andElementTypeGreaterThan(String value) {
            addCriterion("element_type >", value, "elementType");
            return (Criteria) this;
        }

        public Criteria andElementTypeGreaterThanOrEqualTo(String value) {
            addCriterion("element_type >=", value, "elementType");
            return (Criteria) this;
        }

        public Criteria andElementTypeLessThan(String value) {
            addCriterion("element_type <", value, "elementType");
            return (Criteria) this;
        }

        public Criteria andElementTypeLessThanOrEqualTo(String value) {
            addCriterion("element_type <=", value, "elementType");
            return (Criteria) this;
        }

        public Criteria andElementTypeLike(String value) {
            addCriterion("element_type like", value, "elementType");
            return (Criteria) this;
        }

        public Criteria andElementTypeNotLike(String value) {
            addCriterion("element_type not like", value, "elementType");
            return (Criteria) this;
        }

        public Criteria andElementTypeIn(List<String> values) {
            addCriterion("element_type in", values, "elementType");
            return (Criteria) this;
        }

        public Criteria andElementTypeNotIn(List<String> values) {
            addCriterion("element_type not in", values, "elementType");
            return (Criteria) this;
        }

        public Criteria andElementTypeBetween(String value1, String value2) {
            addCriterion("element_type between", value1, value2, "elementType");
            return (Criteria) this;
        }

        public Criteria andElementTypeNotBetween(String value1, String value2) {
            addCriterion("element_type not between", value1, value2, "elementType");
            return (Criteria) this;
        }

        public Criteria andParentIdIsNull() {
            addCriterion("parent_id is null");
            return (Criteria) this;
        }

        public Criteria andParentIdIsNotNull() {
            addCriterion("parent_id is not null");
            return (Criteria) this;
        }

        public Criteria andParentIdEqualTo(Integer value) {
            addCriterion("parent_id =", value, "parentId");
            return (Criteria) this;
        }

        public Criteria andParentIdNotEqualTo(Integer value) {
            addCriterion("parent_id <>", value, "parentId");
            return (Criteria) this;
        }

        public Criteria andParentIdGreaterThan(Integer value) {
            addCriterion("parent_id >", value, "parentId");
            return (Criteria) this;
        }

        public Criteria andParentIdGreaterThanOrEqualTo(Integer value) {
            addCriterion("parent_id >=", value, "parentId");
            return (Criteria) this;
        }

        public Criteria andParentIdLessThan(Integer value) {
            addCriterion("parent_id <", value, "parentId");
            return (Criteria) this;
        }

        public Criteria andParentIdLessThanOrEqualTo(Integer value) {
            addCriterion("parent_id <=", value, "parentId");
            return (Criteria) this;
        }

        public Criteria andParentIdIn(List<Integer> values) {
            addCriterion("parent_id in", values, "parentId");
            return (Criteria) this;
        }

        public Criteria andParentIdNotIn(List<Integer> values) {
            addCriterion("parent_id not in", values, "parentId");
            return (Criteria) this;
        }

        public Criteria andParentIdBetween(Integer value1, Integer value2) {
            addCriterion("parent_id between", value1, value2, "parentId");
            return (Criteria) this;
        }

        public Criteria andParentIdNotBetween(Integer value1, Integer value2) {
            addCriterion("parent_id not between", value1, value2, "parentId");
            return (Criteria) this;
        }

        public Criteria andScriptIdIsNull() {
            addCriterion("script_id is null");
            return (Criteria) this;
        }

        public Criteria andScriptIdIsNotNull() {
            addCriterion("script_id is not null");
            return (Criteria) this;
        }

        public Criteria andScriptIdEqualTo(Integer value) {
            addCriterion("script_id =", value, "scriptId");
            return (Criteria) this;
        }

        public Criteria andScriptIdNotEqualTo(Integer value) {
            addCriterion("script_id <>", value, "scriptId");
            return (Criteria) this;
        }

        public Criteria andScriptIdGreaterThan(Integer value) {
            addCriterion("script_id >", value, "scriptId");
            return (Criteria) this;
        }

        public Criteria andScriptIdGreaterThanOrEqualTo(Integer value) {
            addCriterion("script_id >=", value, "scriptId");
            return (Criteria) this;
        }

        public Criteria andScriptIdLessThan(Integer value) {
            addCriterion("script_id <", value, "scriptId");
            return (Criteria) this;
        }

        public Criteria andScriptIdLessThanOrEqualTo(Integer value) {
            addCriterion("script_id <=", value, "scriptId");
            return (Criteria) this;
        }

        public Criteria andScriptIdIn(List<Integer> values) {
            addCriterion("script_id in", values, "scriptId");
            return (Criteria) this;
        }

        public Criteria andScriptIdNotIn(List<Integer> values) {
            addCriterion("script_id not in", values, "scriptId");
            return (Criteria) this;
        }

        public Criteria andScriptIdBetween(Integer value1, Integer value2) {
            addCriterion("script_id between", value1, value2, "scriptId");
            return (Criteria) this;
        }

        public Criteria andScriptIdNotBetween(Integer value1, Integer value2) {
            addCriterion("script_id not between", value1, value2, "scriptId");
            return (Criteria) this;
        }

        public Criteria andArgusPathIsNull() {
            addCriterion("argus_path is null");
            return (Criteria) this;
        }

        public Criteria andArgusPathIsNotNull() {
            addCriterion("argus_path is not null");
            return (Criteria) this;
        }

        public Criteria andArgusPathEqualTo(String value) {
            addCriterion("argus_path =", value, "argusPath");
            return (Criteria) this;
        }

        public Criteria andArgusPathNotEqualTo(String value) {
            addCriterion("argus_path <>", value, "argusPath");
            return (Criteria) this;
        }

        public Criteria andArgusPathGreaterThan(String value) {
            addCriterion("argus_path >", value, "argusPath");
            return (Criteria) this;
        }

        public Criteria andArgusPathGreaterThanOrEqualTo(String value) {
            addCriterion("argus_path >=", value, "argusPath");
            return (Criteria) this;
        }

        public Criteria andArgusPathLessThan(String value) {
            addCriterion("argus_path <", value, "argusPath");
            return (Criteria) this;
        }

        public Criteria andArgusPathLessThanOrEqualTo(String value) {
            addCriterion("argus_path <=", value, "argusPath");
            return (Criteria) this;
        }

        public Criteria andArgusPathLike(String value) {
            addCriterion("argus_path like", value, "argusPath");
            return (Criteria) this;
        }

        public Criteria andArgusPathNotLike(String value) {
            addCriterion("argus_path not like", value, "argusPath");
            return (Criteria) this;
        }

        public Criteria andArgusPathIn(List<String> values) {
            addCriterion("argus_path in", values, "argusPath");
            return (Criteria) this;
        }

        public Criteria andArgusPathNotIn(List<String> values) {
            addCriterion("argus_path not in", values, "argusPath");
            return (Criteria) this;
        }

        public Criteria andArgusPathBetween(String value1, String value2) {
            addCriterion("argus_path between", value1, value2, "argusPath");
            return (Criteria) this;
        }

        public Criteria andArgusPathNotBetween(String value1, String value2) {
            addCriterion("argus_path not between", value1, value2, "argusPath");
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

        public Criteria andCreateUserIsNull() {
            addCriterion("create_user is null");
            return (Criteria) this;
        }

        public Criteria andCreateUserIsNotNull() {
            addCriterion("create_user is not null");
            return (Criteria) this;
        }

        public Criteria andCreateUserEqualTo(String value) {
            addCriterion("create_user =", value, "createUser");
            return (Criteria) this;
        }

        public Criteria andCreateUserNotEqualTo(String value) {
            addCriterion("create_user <>", value, "createUser");
            return (Criteria) this;
        }

        public Criteria andCreateUserGreaterThan(String value) {
            addCriterion("create_user >", value, "createUser");
            return (Criteria) this;
        }

        public Criteria andCreateUserGreaterThanOrEqualTo(String value) {
            addCriterion("create_user >=", value, "createUser");
            return (Criteria) this;
        }

        public Criteria andCreateUserLessThan(String value) {
            addCriterion("create_user <", value, "createUser");
            return (Criteria) this;
        }

        public Criteria andCreateUserLessThanOrEqualTo(String value) {
            addCriterion("create_user <=", value, "createUser");
            return (Criteria) this;
        }

        public Criteria andCreateUserLike(String value) {
            addCriterion("create_user like", value, "createUser");
            return (Criteria) this;
        }

        public Criteria andCreateUserNotLike(String value) {
            addCriterion("create_user not like", value, "createUser");
            return (Criteria) this;
        }

        public Criteria andCreateUserIn(List<String> values) {
            addCriterion("create_user in", values, "createUser");
            return (Criteria) this;
        }

        public Criteria andCreateUserNotIn(List<String> values) {
            addCriterion("create_user not in", values, "createUser");
            return (Criteria) this;
        }

        public Criteria andCreateUserBetween(String value1, String value2) {
            addCriterion("create_user between", value1, value2, "createUser");
            return (Criteria) this;
        }

        public Criteria andCreateUserNotBetween(String value1, String value2) {
            addCriterion("create_user not between", value1, value2, "createUser");
            return (Criteria) this;
        }

        public Criteria andCreateTimeIsNull() {
            addCriterion("create_time is null");
            return (Criteria) this;
        }

        public Criteria andCreateTimeIsNotNull() {
            addCriterion("create_time is not null");
            return (Criteria) this;
        }

        public Criteria andCreateTimeEqualTo(Date value) {
            addCriterion("create_time =", value, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeNotEqualTo(Date value) {
            addCriterion("create_time <>", value, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeGreaterThan(Date value) {
            addCriterion("create_time >", value, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeGreaterThanOrEqualTo(Date value) {
            addCriterion("create_time >=", value, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeLessThan(Date value) {
            addCriterion("create_time <", value, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeLessThanOrEqualTo(Date value) {
            addCriterion("create_time <=", value, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeIn(List<Date> values) {
            addCriterion("create_time in", values, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeNotIn(List<Date> values) {
            addCriterion("create_time not in", values, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeBetween(Date value1, Date value2) {
            addCriterion("create_time between", value1, value2, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeNotBetween(Date value1, Date value2) {
            addCriterion("create_time not between", value1, value2, "createTime");
            return (Criteria) this;
        }

        public Criteria andSeqIsNull() {
            addCriterion("seq is null");
            return (Criteria) this;
        }

        public Criteria andSeqIsNotNull() {
            addCriterion("seq is not null");
            return (Criteria) this;
        }

        public Criteria andSeqEqualTo(Integer value) {
            addCriterion("seq =", value, "seq");
            return (Criteria) this;
        }

        public Criteria andSeqNotEqualTo(Integer value) {
            addCriterion("seq <>", value, "seq");
            return (Criteria) this;
        }

        public Criteria andSeqGreaterThan(Integer value) {
            addCriterion("seq >", value, "seq");
            return (Criteria) this;
        }

        public Criteria andSeqGreaterThanOrEqualTo(Integer value) {
            addCriterion("seq >=", value, "seq");
            return (Criteria) this;
        }

        public Criteria andSeqLessThan(Integer value) {
            addCriterion("seq <", value, "seq");
            return (Criteria) this;
        }

        public Criteria andSeqLessThanOrEqualTo(Integer value) {
            addCriterion("seq <=", value, "seq");
            return (Criteria) this;
        }

        public Criteria andSeqIn(List<Integer> values) {
            addCriterion("seq in", values, "seq");
            return (Criteria) this;
        }

        public Criteria andSeqNotIn(List<Integer> values) {
            addCriterion("seq not in", values, "seq");
            return (Criteria) this;
        }

        public Criteria andSeqBetween(Integer value1, Integer value2) {
            addCriterion("seq between", value1, value2, "seq");
            return (Criteria) this;
        }

        public Criteria andSeqNotBetween(Integer value1, Integer value2) {
            addCriterion("seq not between", value1, value2, "seq");
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
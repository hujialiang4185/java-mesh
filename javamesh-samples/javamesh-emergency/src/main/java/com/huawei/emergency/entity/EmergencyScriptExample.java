package com.huawei.emergency.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class EmergencyScriptExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public EmergencyScriptExample() {
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

        public Criteria andScriptNameIsNull() {
            addCriterion("script_name is null");
            return (Criteria) this;
        }

        public Criteria andScriptNameIsNotNull() {
            addCriterion("script_name is not null");
            return (Criteria) this;
        }

        public Criteria andScriptNameEqualTo(String value) {
            addCriterion("script_name =", value, "scriptName");
            return (Criteria) this;
        }

        public Criteria andScriptNameNotEqualTo(String value) {
            addCriterion("script_name <>", value, "scriptName");
            return (Criteria) this;
        }

        public Criteria andScriptNameGreaterThan(String value) {
            addCriterion("script_name >", value, "scriptName");
            return (Criteria) this;
        }

        public Criteria andScriptNameGreaterThanOrEqualTo(String value) {
            addCriterion("script_name >=", value, "scriptName");
            return (Criteria) this;
        }

        public Criteria andScriptNameLessThan(String value) {
            addCriterion("script_name <", value, "scriptName");
            return (Criteria) this;
        }

        public Criteria andScriptNameLessThanOrEqualTo(String value) {
            addCriterion("script_name <=", value, "scriptName");
            return (Criteria) this;
        }

        public Criteria andScriptNameLike(String value) {
            addCriterion("script_name like", value, "scriptName");
            return (Criteria) this;
        }

        public Criteria andScriptNameNotLike(String value) {
            addCriterion("script_name not like", value, "scriptName");
            return (Criteria) this;
        }

        public Criteria andScriptNameIn(List<String> values) {
            addCriterion("script_name in", values, "scriptName");
            return (Criteria) this;
        }

        public Criteria andScriptNameNotIn(List<String> values) {
            addCriterion("script_name not in", values, "scriptName");
            return (Criteria) this;
        }

        public Criteria andScriptNameBetween(String value1, String value2) {
            addCriterion("script_name between", value1, value2, "scriptName");
            return (Criteria) this;
        }

        public Criteria andScriptNameNotBetween(String value1, String value2) {
            addCriterion("script_name not between", value1, value2, "scriptName");
            return (Criteria) this;
        }

        public Criteria andIsPublicIsNull() {
            addCriterion("is_public is null");
            return (Criteria) this;
        }

        public Criteria andIsPublicIsNotNull() {
            addCriterion("is_public is not null");
            return (Criteria) this;
        }

        public Criteria andIsPublicEqualTo(String value) {
            addCriterion("is_public =", value, "isPublic");
            return (Criteria) this;
        }

        public Criteria andIsPublicNotEqualTo(String value) {
            addCriterion("is_public <>", value, "isPublic");
            return (Criteria) this;
        }

        public Criteria andIsPublicGreaterThan(String value) {
            addCriterion("is_public >", value, "isPublic");
            return (Criteria) this;
        }

        public Criteria andIsPublicGreaterThanOrEqualTo(String value) {
            addCriterion("is_public >=", value, "isPublic");
            return (Criteria) this;
        }

        public Criteria andIsPublicLessThan(String value) {
            addCriterion("is_public <", value, "isPublic");
            return (Criteria) this;
        }

        public Criteria andIsPublicLessThanOrEqualTo(String value) {
            addCriterion("is_public <=", value, "isPublic");
            return (Criteria) this;
        }

        public Criteria andIsPublicLike(String value) {
            addCriterion("is_public like", value, "isPublic");
            return (Criteria) this;
        }

        public Criteria andIsPublicNotLike(String value) {
            addCriterion("is_public not like", value, "isPublic");
            return (Criteria) this;
        }

        public Criteria andIsPublicIn(List<String> values) {
            addCriterion("is_public in", values, "isPublic");
            return (Criteria) this;
        }

        public Criteria andIsPublicNotIn(List<String> values) {
            addCriterion("is_public not in", values, "isPublic");
            return (Criteria) this;
        }

        public Criteria andIsPublicBetween(String value1, String value2) {
            addCriterion("is_public between", value1, value2, "isPublic");
            return (Criteria) this;
        }

        public Criteria andIsPublicNotBetween(String value1, String value2) {
            addCriterion("is_public not between", value1, value2, "isPublic");
            return (Criteria) this;
        }

        public Criteria andScriptTypeIsNull() {
            addCriterion("script_type is null");
            return (Criteria) this;
        }

        public Criteria andScriptTypeIsNotNull() {
            addCriterion("script_type is not null");
            return (Criteria) this;
        }

        public Criteria andScriptTypeEqualTo(String value) {
            addCriterion("script_type =", value, "scriptType");
            return (Criteria) this;
        }

        public Criteria andScriptTypeNotEqualTo(String value) {
            addCriterion("script_type <>", value, "scriptType");
            return (Criteria) this;
        }

        public Criteria andScriptTypeGreaterThan(String value) {
            addCriterion("script_type >", value, "scriptType");
            return (Criteria) this;
        }

        public Criteria andScriptTypeGreaterThanOrEqualTo(String value) {
            addCriterion("script_type >=", value, "scriptType");
            return (Criteria) this;
        }

        public Criteria andScriptTypeLessThan(String value) {
            addCriterion("script_type <", value, "scriptType");
            return (Criteria) this;
        }

        public Criteria andScriptTypeLessThanOrEqualTo(String value) {
            addCriterion("script_type <=", value, "scriptType");
            return (Criteria) this;
        }

        public Criteria andScriptTypeLike(String value) {
            addCriterion("script_type like", value, "scriptType");
            return (Criteria) this;
        }

        public Criteria andScriptTypeNotLike(String value) {
            addCriterion("script_type not like", value, "scriptType");
            return (Criteria) this;
        }

        public Criteria andScriptTypeIn(List<String> values) {
            addCriterion("script_type in", values, "scriptType");
            return (Criteria) this;
        }

        public Criteria andScriptTypeNotIn(List<String> values) {
            addCriterion("script_type not in", values, "scriptType");
            return (Criteria) this;
        }

        public Criteria andScriptTypeBetween(String value1, String value2) {
            addCriterion("script_type between", value1, value2, "scriptType");
            return (Criteria) this;
        }

        public Criteria andScriptTypeNotBetween(String value1, String value2) {
            addCriterion("script_type not between", value1, value2, "scriptType");
            return (Criteria) this;
        }

        public Criteria andSubmitInfoIsNull() {
            addCriterion("submit_info is null");
            return (Criteria) this;
        }

        public Criteria andSubmitInfoIsNotNull() {
            addCriterion("submit_info is not null");
            return (Criteria) this;
        }

        public Criteria andSubmitInfoEqualTo(String value) {
            addCriterion("submit_info =", value, "submitInfo");
            return (Criteria) this;
        }

        public Criteria andSubmitInfoNotEqualTo(String value) {
            addCriterion("submit_info <>", value, "submitInfo");
            return (Criteria) this;
        }

        public Criteria andSubmitInfoGreaterThan(String value) {
            addCriterion("submit_info >", value, "submitInfo");
            return (Criteria) this;
        }

        public Criteria andSubmitInfoGreaterThanOrEqualTo(String value) {
            addCriterion("submit_info >=", value, "submitInfo");
            return (Criteria) this;
        }

        public Criteria andSubmitInfoLessThan(String value) {
            addCriterion("submit_info <", value, "submitInfo");
            return (Criteria) this;
        }

        public Criteria andSubmitInfoLessThanOrEqualTo(String value) {
            addCriterion("submit_info <=", value, "submitInfo");
            return (Criteria) this;
        }

        public Criteria andSubmitInfoLike(String value) {
            addCriterion("submit_info like", value, "submitInfo");
            return (Criteria) this;
        }

        public Criteria andSubmitInfoNotLike(String value) {
            addCriterion("submit_info not like", value, "submitInfo");
            return (Criteria) this;
        }

        public Criteria andSubmitInfoIn(List<String> values) {
            addCriterion("submit_info in", values, "submitInfo");
            return (Criteria) this;
        }

        public Criteria andSubmitInfoNotIn(List<String> values) {
            addCriterion("submit_info not in", values, "submitInfo");
            return (Criteria) this;
        }

        public Criteria andSubmitInfoBetween(String value1, String value2) {
            addCriterion("submit_info between", value1, value2, "submitInfo");
            return (Criteria) this;
        }

        public Criteria andSubmitInfoNotBetween(String value1, String value2) {
            addCriterion("submit_info not between", value1, value2, "submitInfo");
            return (Criteria) this;
        }

        public Criteria andHavePasswordIsNull() {
            addCriterion("have_password is null");
            return (Criteria) this;
        }

        public Criteria andHavePasswordIsNotNull() {
            addCriterion("have_password is not null");
            return (Criteria) this;
        }

        public Criteria andHavePasswordEqualTo(String value) {
            addCriterion("have_password =", value, "havePassword");
            return (Criteria) this;
        }

        public Criteria andHavePasswordNotEqualTo(String value) {
            addCriterion("have_password <>", value, "havePassword");
            return (Criteria) this;
        }

        public Criteria andHavePasswordGreaterThan(String value) {
            addCriterion("have_password >", value, "havePassword");
            return (Criteria) this;
        }

        public Criteria andHavePasswordGreaterThanOrEqualTo(String value) {
            addCriterion("have_password >=", value, "havePassword");
            return (Criteria) this;
        }

        public Criteria andHavePasswordLessThan(String value) {
            addCriterion("have_password <", value, "havePassword");
            return (Criteria) this;
        }

        public Criteria andHavePasswordLessThanOrEqualTo(String value) {
            addCriterion("have_password <=", value, "havePassword");
            return (Criteria) this;
        }

        public Criteria andHavePasswordLike(String value) {
            addCriterion("have_password like", value, "havePassword");
            return (Criteria) this;
        }

        public Criteria andHavePasswordNotLike(String value) {
            addCriterion("have_password not like", value, "havePassword");
            return (Criteria) this;
        }

        public Criteria andHavePasswordIn(List<String> values) {
            addCriterion("have_password in", values, "havePassword");
            return (Criteria) this;
        }

        public Criteria andHavePasswordNotIn(List<String> values) {
            addCriterion("have_password not in", values, "havePassword");
            return (Criteria) this;
        }

        public Criteria andHavePasswordBetween(String value1, String value2) {
            addCriterion("have_password between", value1, value2, "havePassword");
            return (Criteria) this;
        }

        public Criteria andHavePasswordNotBetween(String value1, String value2) {
            addCriterion("have_password not between", value1, value2, "havePassword");
            return (Criteria) this;
        }

        public Criteria andPasswordModeIsNull() {
            addCriterion("password_mode is null");
            return (Criteria) this;
        }

        public Criteria andPasswordModeIsNotNull() {
            addCriterion("password_mode is not null");
            return (Criteria) this;
        }

        public Criteria andPasswordModeEqualTo(String value) {
            addCriterion("password_mode =", value, "passwordMode");
            return (Criteria) this;
        }

        public Criteria andPasswordModeNotEqualTo(String value) {
            addCriterion("password_mode <>", value, "passwordMode");
            return (Criteria) this;
        }

        public Criteria andPasswordModeGreaterThan(String value) {
            addCriterion("password_mode >", value, "passwordMode");
            return (Criteria) this;
        }

        public Criteria andPasswordModeGreaterThanOrEqualTo(String value) {
            addCriterion("password_mode >=", value, "passwordMode");
            return (Criteria) this;
        }

        public Criteria andPasswordModeLessThan(String value) {
            addCriterion("password_mode <", value, "passwordMode");
            return (Criteria) this;
        }

        public Criteria andPasswordModeLessThanOrEqualTo(String value) {
            addCriterion("password_mode <=", value, "passwordMode");
            return (Criteria) this;
        }

        public Criteria andPasswordModeLike(String value) {
            addCriterion("password_mode like", value, "passwordMode");
            return (Criteria) this;
        }

        public Criteria andPasswordModeNotLike(String value) {
            addCriterion("password_mode not like", value, "passwordMode");
            return (Criteria) this;
        }

        public Criteria andPasswordModeIn(List<String> values) {
            addCriterion("password_mode in", values, "passwordMode");
            return (Criteria) this;
        }

        public Criteria andPasswordModeNotIn(List<String> values) {
            addCriterion("password_mode not in", values, "passwordMode");
            return (Criteria) this;
        }

        public Criteria andPasswordModeBetween(String value1, String value2) {
            addCriterion("password_mode between", value1, value2, "passwordMode");
            return (Criteria) this;
        }

        public Criteria andPasswordModeNotBetween(String value1, String value2) {
            addCriterion("password_mode not between", value1, value2, "passwordMode");
            return (Criteria) this;
        }

        public Criteria andPasswordIsNull() {
            addCriterion("password is null");
            return (Criteria) this;
        }

        public Criteria andPasswordIsNotNull() {
            addCriterion("password is not null");
            return (Criteria) this;
        }

        public Criteria andPasswordEqualTo(String value) {
            addCriterion("password =", value, "password");
            return (Criteria) this;
        }

        public Criteria andPasswordNotEqualTo(String value) {
            addCriterion("password <>", value, "password");
            return (Criteria) this;
        }

        public Criteria andPasswordGreaterThan(String value) {
            addCriterion("password >", value, "password");
            return (Criteria) this;
        }

        public Criteria andPasswordGreaterThanOrEqualTo(String value) {
            addCriterion("password >=", value, "password");
            return (Criteria) this;
        }

        public Criteria andPasswordLessThan(String value) {
            addCriterion("password <", value, "password");
            return (Criteria) this;
        }

        public Criteria andPasswordLessThanOrEqualTo(String value) {
            addCriterion("password <=", value, "password");
            return (Criteria) this;
        }

        public Criteria andPasswordLike(String value) {
            addCriterion("password like", value, "password");
            return (Criteria) this;
        }

        public Criteria andPasswordNotLike(String value) {
            addCriterion("password not like", value, "password");
            return (Criteria) this;
        }

        public Criteria andPasswordIn(List<String> values) {
            addCriterion("password in", values, "password");
            return (Criteria) this;
        }

        public Criteria andPasswordNotIn(List<String> values) {
            addCriterion("password not in", values, "password");
            return (Criteria) this;
        }

        public Criteria andPasswordBetween(String value1, String value2) {
            addCriterion("password between", value1, value2, "password");
            return (Criteria) this;
        }

        public Criteria andPasswordNotBetween(String value1, String value2) {
            addCriterion("password not between", value1, value2, "password");
            return (Criteria) this;
        }

        public Criteria andServerUserIsNull() {
            addCriterion("server_user is null");
            return (Criteria) this;
        }

        public Criteria andServerUserIsNotNull() {
            addCriterion("server_user is not null");
            return (Criteria) this;
        }

        public Criteria andServerUserEqualTo(String value) {
            addCriterion("server_user =", value, "serverUser");
            return (Criteria) this;
        }

        public Criteria andServerUserNotEqualTo(String value) {
            addCriterion("server_user <>", value, "serverUser");
            return (Criteria) this;
        }

        public Criteria andServerUserGreaterThan(String value) {
            addCriterion("server_user >", value, "serverUser");
            return (Criteria) this;
        }

        public Criteria andServerUserGreaterThanOrEqualTo(String value) {
            addCriterion("server_user >=", value, "serverUser");
            return (Criteria) this;
        }

        public Criteria andServerUserLessThan(String value) {
            addCriterion("server_user <", value, "serverUser");
            return (Criteria) this;
        }

        public Criteria andServerUserLessThanOrEqualTo(String value) {
            addCriterion("server_user <=", value, "serverUser");
            return (Criteria) this;
        }

        public Criteria andServerUserLike(String value) {
            addCriterion("server_user like", value, "serverUser");
            return (Criteria) this;
        }

        public Criteria andServerUserNotLike(String value) {
            addCriterion("server_user not like", value, "serverUser");
            return (Criteria) this;
        }

        public Criteria andServerUserIn(List<String> values) {
            addCriterion("server_user in", values, "serverUser");
            return (Criteria) this;
        }

        public Criteria andServerUserNotIn(List<String> values) {
            addCriterion("server_user not in", values, "serverUser");
            return (Criteria) this;
        }

        public Criteria andServerUserBetween(String value1, String value2) {
            addCriterion("server_user between", value1, value2, "serverUser");
            return (Criteria) this;
        }

        public Criteria andServerUserNotBetween(String value1, String value2) {
            addCriterion("server_user not between", value1, value2, "serverUser");
            return (Criteria) this;
        }

        public Criteria andServerIpIsNull() {
            addCriterion("server_ip is null");
            return (Criteria) this;
        }

        public Criteria andServerIpIsNotNull() {
            addCriterion("server_ip is not null");
            return (Criteria) this;
        }

        public Criteria andServerIpEqualTo(String value) {
            addCriterion("server_ip =", value, "serverIp");
            return (Criteria) this;
        }

        public Criteria andServerIpNotEqualTo(String value) {
            addCriterion("server_ip <>", value, "serverIp");
            return (Criteria) this;
        }

        public Criteria andServerIpGreaterThan(String value) {
            addCriterion("server_ip >", value, "serverIp");
            return (Criteria) this;
        }

        public Criteria andServerIpGreaterThanOrEqualTo(String value) {
            addCriterion("server_ip >=", value, "serverIp");
            return (Criteria) this;
        }

        public Criteria andServerIpLessThan(String value) {
            addCriterion("server_ip <", value, "serverIp");
            return (Criteria) this;
        }

        public Criteria andServerIpLessThanOrEqualTo(String value) {
            addCriterion("server_ip <=", value, "serverIp");
            return (Criteria) this;
        }

        public Criteria andServerIpLike(String value) {
            addCriterion("server_ip like", value, "serverIp");
            return (Criteria) this;
        }

        public Criteria andServerIpNotLike(String value) {
            addCriterion("server_ip not like", value, "serverIp");
            return (Criteria) this;
        }

        public Criteria andServerIpIn(List<String> values) {
            addCriterion("server_ip in", values, "serverIp");
            return (Criteria) this;
        }

        public Criteria andServerIpNotIn(List<String> values) {
            addCriterion("server_ip not in", values, "serverIp");
            return (Criteria) this;
        }

        public Criteria andServerIpBetween(String value1, String value2) {
            addCriterion("server_ip between", value1, value2, "serverIp");
            return (Criteria) this;
        }

        public Criteria andServerIpNotBetween(String value1, String value2) {
            addCriterion("server_ip not between", value1, value2, "serverIp");
            return (Criteria) this;
        }

        public Criteria andContentIsNull() {
            addCriterion("content is null");
            return (Criteria) this;
        }

        public Criteria andContentIsNotNull() {
            addCriterion("content is not null");
            return (Criteria) this;
        }

        public Criteria andContentEqualTo(String value) {
            addCriterion("content =", value, "content");
            return (Criteria) this;
        }

        public Criteria andContentNotEqualTo(String value) {
            addCriterion("content <>", value, "content");
            return (Criteria) this;
        }

        public Criteria andContentGreaterThan(String value) {
            addCriterion("content >", value, "content");
            return (Criteria) this;
        }

        public Criteria andContentGreaterThanOrEqualTo(String value) {
            addCriterion("content >=", value, "content");
            return (Criteria) this;
        }

        public Criteria andContentLessThan(String value) {
            addCriterion("content <", value, "content");
            return (Criteria) this;
        }

        public Criteria andContentLessThanOrEqualTo(String value) {
            addCriterion("content <=", value, "content");
            return (Criteria) this;
        }

        public Criteria andContentLike(String value) {
            addCriterion("content like", value, "content");
            return (Criteria) this;
        }

        public Criteria andContentNotLike(String value) {
            addCriterion("content not like", value, "content");
            return (Criteria) this;
        }

        public Criteria andContentIn(List<String> values) {
            addCriterion("content in", values, "content");
            return (Criteria) this;
        }

        public Criteria andContentNotIn(List<String> values) {
            addCriterion("content not in", values, "content");
            return (Criteria) this;
        }

        public Criteria andContentBetween(String value1, String value2) {
            addCriterion("content between", value1, value2, "content");
            return (Criteria) this;
        }

        public Criteria andContentNotBetween(String value1, String value2) {
            addCriterion("content not between", value1, value2, "content");
            return (Criteria) this;
        }

        public Criteria andScriptUserIsNull() {
            addCriterion("script_user is null");
            return (Criteria) this;
        }

        public Criteria andScriptUserIsNotNull() {
            addCriterion("script_user is not null");
            return (Criteria) this;
        }

        public Criteria andScriptUserEqualTo(String value) {
            addCriterion("script_user =", value, "scriptUser");
            return (Criteria) this;
        }

        public Criteria andScriptUserNotEqualTo(String value) {
            addCriterion("script_user <>", value, "scriptUser");
            return (Criteria) this;
        }

        public Criteria andScriptUserGreaterThan(String value) {
            addCriterion("script_user >", value, "scriptUser");
            return (Criteria) this;
        }

        public Criteria andScriptUserGreaterThanOrEqualTo(String value) {
            addCriterion("script_user >=", value, "scriptUser");
            return (Criteria) this;
        }

        public Criteria andScriptUserLessThan(String value) {
            addCriterion("script_user <", value, "scriptUser");
            return (Criteria) this;
        }

        public Criteria andScriptUserLessThanOrEqualTo(String value) {
            addCriterion("script_user <=", value, "scriptUser");
            return (Criteria) this;
        }

        public Criteria andScriptUserLike(String value) {
            addCriterion("script_user like", value, "scriptUser");
            return (Criteria) this;
        }

        public Criteria andScriptUserNotLike(String value) {
            addCriterion("script_user not like", value, "scriptUser");
            return (Criteria) this;
        }

        public Criteria andScriptUserIn(List<String> values) {
            addCriterion("script_user in", values, "scriptUser");
            return (Criteria) this;
        }

        public Criteria andScriptUserNotIn(List<String> values) {
            addCriterion("script_user not in", values, "scriptUser");
            return (Criteria) this;
        }

        public Criteria andScriptUserBetween(String value1, String value2) {
            addCriterion("script_user between", value1, value2, "scriptUser");
            return (Criteria) this;
        }

        public Criteria andScriptUserNotBetween(String value1, String value2) {
            addCriterion("script_user not between", value1, value2, "scriptUser");
            return (Criteria) this;
        }

        public Criteria andScriptGroupIsNull() {
            addCriterion("script_group is null");
            return (Criteria) this;
        }

        public Criteria andScriptGroupIsNotNull() {
            addCriterion("script_group is not null");
            return (Criteria) this;
        }

        public Criteria andScriptGroupEqualTo(String value) {
            addCriterion("script_group =", value, "scriptGroup");
            return (Criteria) this;
        }

        public Criteria andScriptGroupNotEqualTo(String value) {
            addCriterion("script_group <>", value, "scriptGroup");
            return (Criteria) this;
        }

        public Criteria andScriptGroupGreaterThan(String value) {
            addCriterion("script_group >", value, "scriptGroup");
            return (Criteria) this;
        }

        public Criteria andScriptGroupGreaterThanOrEqualTo(String value) {
            addCriterion("script_group >=", value, "scriptGroup");
            return (Criteria) this;
        }

        public Criteria andScriptGroupLessThan(String value) {
            addCriterion("script_group <", value, "scriptGroup");
            return (Criteria) this;
        }

        public Criteria andScriptGroupLessThanOrEqualTo(String value) {
            addCriterion("script_group <=", value, "scriptGroup");
            return (Criteria) this;
        }

        public Criteria andScriptGroupLike(String value) {
            addCriterion("script_group like", value, "scriptGroup");
            return (Criteria) this;
        }

        public Criteria andScriptGroupNotLike(String value) {
            addCriterion("script_group not like", value, "scriptGroup");
            return (Criteria) this;
        }

        public Criteria andScriptGroupIn(List<String> values) {
            addCriterion("script_group in", values, "scriptGroup");
            return (Criteria) this;
        }

        public Criteria andScriptGroupNotIn(List<String> values) {
            addCriterion("script_group not in", values, "scriptGroup");
            return (Criteria) this;
        }

        public Criteria andScriptGroupBetween(String value1, String value2) {
            addCriterion("script_group between", value1, value2, "scriptGroup");
            return (Criteria) this;
        }

        public Criteria andScriptGroupNotBetween(String value1, String value2) {
            addCriterion("script_group not between", value1, value2, "scriptGroup");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeIsNull() {
            addCriterion("update_time is null");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeIsNotNull() {
            addCriterion("update_time is not null");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeEqualTo(Date value) {
            addCriterion("update_time =", value, "updateTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeNotEqualTo(Date value) {
            addCriterion("update_time <>", value, "updateTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeGreaterThan(Date value) {
            addCriterion("update_time >", value, "updateTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeGreaterThanOrEqualTo(Date value) {
            addCriterion("update_time >=", value, "updateTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeLessThan(Date value) {
            addCriterion("update_time <", value, "updateTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeLessThanOrEqualTo(Date value) {
            addCriterion("update_time <=", value, "updateTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeIn(List<Date> values) {
            addCriterion("update_time in", values, "updateTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeNotIn(List<Date> values) {
            addCriterion("update_time not in", values, "updateTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeBetween(Date value1, Date value2) {
            addCriterion("update_time between", value1, value2, "updateTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeNotBetween(Date value1, Date value2) {
            addCriterion("update_time not between", value1, value2, "updateTime");
            return (Criteria) this;
        }

        public Criteria andParamIsNull() {
            addCriterion("param is null");
            return (Criteria) this;
        }

        public Criteria andParamIsNotNull() {
            addCriterion("param is not null");
            return (Criteria) this;
        }

        public Criteria andParamEqualTo(String value) {
            addCriterion("param =", value, "param");
            return (Criteria) this;
        }

        public Criteria andParamNotEqualTo(String value) {
            addCriterion("param <>", value, "param");
            return (Criteria) this;
        }

        public Criteria andParamGreaterThan(String value) {
            addCriterion("param >", value, "param");
            return (Criteria) this;
        }

        public Criteria andParamGreaterThanOrEqualTo(String value) {
            addCriterion("param >=", value, "param");
            return (Criteria) this;
        }

        public Criteria andParamLessThan(String value) {
            addCriterion("param <", value, "param");
            return (Criteria) this;
        }

        public Criteria andParamLessThanOrEqualTo(String value) {
            addCriterion("param <=", value, "param");
            return (Criteria) this;
        }

        public Criteria andParamLike(String value) {
            addCriterion("param like", value, "param");
            return (Criteria) this;
        }

        public Criteria andParamNotLike(String value) {
            addCriterion("param not like", value, "param");
            return (Criteria) this;
        }

        public Criteria andParamIn(List<String> values) {
            addCriterion("param in", values, "param");
            return (Criteria) this;
        }

        public Criteria andParamNotIn(List<String> values) {
            addCriterion("param not in", values, "param");
            return (Criteria) this;
        }

        public Criteria andParamBetween(String value1, String value2) {
            addCriterion("param between", value1, value2, "param");
            return (Criteria) this;
        }

        public Criteria andParamNotBetween(String value1, String value2) {
            addCriterion("param not between", value1, value2, "param");
            return (Criteria) this;
        }

        public Criteria andScriptStatusIsNull() {
            addCriterion("script_status is null");
            return (Criteria) this;
        }

        public Criteria andScriptStatusIsNotNull() {
            addCriterion("script_status is not null");
            return (Criteria) this;
        }

        public Criteria andScriptStatusEqualTo(String value) {
            addCriterion("script_status =", value, "scriptStatus");
            return (Criteria) this;
        }

        public Criteria andScriptStatusNotEqualTo(String value) {
            addCriterion("script_status <>", value, "scriptStatus");
            return (Criteria) this;
        }

        public Criteria andScriptStatusGreaterThan(String value) {
            addCriterion("script_status >", value, "scriptStatus");
            return (Criteria) this;
        }

        public Criteria andScriptStatusGreaterThanOrEqualTo(String value) {
            addCriterion("script_status >=", value, "scriptStatus");
            return (Criteria) this;
        }

        public Criteria andScriptStatusLessThan(String value) {
            addCriterion("script_status <", value, "scriptStatus");
            return (Criteria) this;
        }

        public Criteria andScriptStatusLessThanOrEqualTo(String value) {
            addCriterion("script_status <=", value, "scriptStatus");
            return (Criteria) this;
        }

        public Criteria andScriptStatusLike(String value) {
            addCriterion("script_status like", value, "scriptStatus");
            return (Criteria) this;
        }

        public Criteria andScriptStatusNotLike(String value) {
            addCriterion("script_status not like", value, "scriptStatus");
            return (Criteria) this;
        }

        public Criteria andScriptStatusIn(List<String> values) {
            addCriterion("script_status in", values, "scriptStatus");
            return (Criteria) this;
        }

        public Criteria andScriptStatusNotIn(List<String> values) {
            addCriterion("script_status not in", values, "scriptStatus");
            return (Criteria) this;
        }

        public Criteria andScriptStatusBetween(String value1, String value2) {
            addCriterion("script_status between", value1, value2, "scriptStatus");
            return (Criteria) this;
        }

        public Criteria andScriptStatusNotBetween(String value1, String value2) {
            addCriterion("script_status not between", value1, value2, "scriptStatus");
            return (Criteria) this;
        }

        public Criteria andApproverIsNull() {
            addCriterion("approver is null");
            return (Criteria) this;
        }

        public Criteria andApproverIsNotNull() {
            addCriterion("approver is not null");
            return (Criteria) this;
        }

        public Criteria andApproverEqualTo(String value) {
            addCriterion("approver =", value, "approver");
            return (Criteria) this;
        }

        public Criteria andApproverNotEqualTo(String value) {
            addCriterion("approver <>", value, "approver");
            return (Criteria) this;
        }

        public Criteria andApproverGreaterThan(String value) {
            addCriterion("approver >", value, "approver");
            return (Criteria) this;
        }

        public Criteria andApproverGreaterThanOrEqualTo(String value) {
            addCriterion("approver >=", value, "approver");
            return (Criteria) this;
        }

        public Criteria andApproverLessThan(String value) {
            addCriterion("approver <", value, "approver");
            return (Criteria) this;
        }

        public Criteria andApproverLessThanOrEqualTo(String value) {
            addCriterion("approver <=", value, "approver");
            return (Criteria) this;
        }

        public Criteria andApproverLike(String value) {
            addCriterion("approver like", value, "approver");
            return (Criteria) this;
        }

        public Criteria andApproverNotLike(String value) {
            addCriterion("approver not like", value, "approver");
            return (Criteria) this;
        }

        public Criteria andApproverIn(List<String> values) {
            addCriterion("approver in", values, "approver");
            return (Criteria) this;
        }

        public Criteria andApproverNotIn(List<String> values) {
            addCriterion("approver not in", values, "approver");
            return (Criteria) this;
        }

        public Criteria andApproverBetween(String value1, String value2) {
            addCriterion("approver between", value1, value2, "approver");
            return (Criteria) this;
        }

        public Criteria andApproverNotBetween(String value1, String value2) {
            addCriterion("approver not between", value1, value2, "approver");
            return (Criteria) this;
        }

        public Criteria andCommentIsNull() {
            addCriterion("comment is null");
            return (Criteria) this;
        }

        public Criteria andCommentIsNotNull() {
            addCriterion("comment is not null");
            return (Criteria) this;
        }

        public Criteria andCommentEqualTo(String value) {
            addCriterion("comment =", value, "comment");
            return (Criteria) this;
        }

        public Criteria andCommentNotEqualTo(String value) {
            addCriterion("comment <>", value, "comment");
            return (Criteria) this;
        }

        public Criteria andCommentGreaterThan(String value) {
            addCriterion("comment >", value, "comment");
            return (Criteria) this;
        }

        public Criteria andCommentGreaterThanOrEqualTo(String value) {
            addCriterion("comment >=", value, "comment");
            return (Criteria) this;
        }

        public Criteria andCommentLessThan(String value) {
            addCriterion("comment <", value, "comment");
            return (Criteria) this;
        }

        public Criteria andCommentLessThanOrEqualTo(String value) {
            addCriterion("comment <=", value, "comment");
            return (Criteria) this;
        }

        public Criteria andCommentLike(String value) {
            addCriterion("comment like", value, "comment");
            return (Criteria) this;
        }

        public Criteria andCommentNotLike(String value) {
            addCriterion("comment not like", value, "comment");
            return (Criteria) this;
        }

        public Criteria andCommentIn(List<String> values) {
            addCriterion("comment in", values, "comment");
            return (Criteria) this;
        }

        public Criteria andCommentNotIn(List<String> values) {
            addCriterion("comment not in", values, "comment");
            return (Criteria) this;
        }

        public Criteria andCommentBetween(String value1, String value2) {
            addCriterion("comment between", value1, value2, "comment");
            return (Criteria) this;
        }

        public Criteria andCommentNotBetween(String value1, String value2) {
            addCriterion("comment not between", value1, value2, "comment");
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
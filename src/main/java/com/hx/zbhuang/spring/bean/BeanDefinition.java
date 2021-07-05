package com.hx.zbhuang.spring.bean;

public class BeanDefinition {

        private Class beanClass;
        private ScopeEum scopeEum;

    public Class getBeanClass() {
        return beanClass;
    }

    public void setBeanClass(Class beanClass) {
        this.beanClass = beanClass;
    }

    public ScopeEum getScopeEum() {
        return scopeEum;
    }

    public void setScopeEum(ScopeEum scopeEum) {
        this.scopeEum = scopeEum;
    }
}

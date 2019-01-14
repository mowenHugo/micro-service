package com.hugo.common.pushcenter.provider.sdk;

public class RequestHolder {
    private HugoHashMap header;
    private String bodyContent;

    public RequestHolder() {
    }

    public HugoHashMap getHeaderMap() {
        return this.header;
    }

    public void setHeaderMap(HugoHashMap protocalMustParams) {
        this.header = protocalMustParams;
    }

    public String getBodyContent() {
        return this.bodyContent;
    }

    public void setBodyContent(String bodyContent) {
        this.bodyContent = bodyContent;
    }
}

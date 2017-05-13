package camelcase.searchemall;

import java.io.Serializable;

public class WebPageProperties {

    private String mWebPageUrl;
    private boolean mIsJsEnable = false;
    private String mPageName;

    public WebPageProperties() {
    }

    public WebPageProperties(String pageName, String url, boolean jsEnable) {
        mWebPageUrl = url;
        mIsJsEnable = jsEnable;
        mPageName = pageName;
    }

    public String getWebPageUrl() {
        if (null == mWebPageUrl) throw new NullPointerException("Webpage URL is empty");
        else return mWebPageUrl;
    }

    public void setWebPageUrl(String mWebPageUrl) {
        this.mWebPageUrl = mWebPageUrl;
    }

    public boolean isJsEnable() {
        return mIsJsEnable;
    }

    public void seIsJsEnable(boolean mIsJsEnable) {
        this.mIsJsEnable = mIsJsEnable;
    }

    public String getPageName() {
        return mPageName;
    }

    public void setPageName(String mPageName) {
        this.mPageName = mPageName;
    }
}

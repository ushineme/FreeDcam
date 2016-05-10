package com.freedviewer.holder;

import java.net.URL;

/**
 * Created by Ingo on 27.12.2015.
 */
class UrlHolder extends BaseHolder
{
    private URL url;

    public UrlHolder(URL url)
    {
        this.url=url;
    }

    public URL getUrl()
    {
        return url;
    }
}
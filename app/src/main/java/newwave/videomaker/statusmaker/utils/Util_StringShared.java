package newwave.videomaker.statusmaker.utils;

public enum Util_StringShared
{
    MYGST_API(""),  MYGST_KEY("");

    public String mDefaultValue;

    Util_StringShared(String paramString)
    {
        mDefaultValue = paramString;
    }

    public String getDefaultValue()
    {
        return mDefaultValue;
    }

    public String getName()
    {
        return name();
    }
}

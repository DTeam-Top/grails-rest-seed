import static org.grails.plugins.databinding.AbstractDataBindingGrailsPlugin.DEFAULT_DATE_FORMATS

// 支持的时间日期格式:
// 2018-01-01 00:00:00
// 2018-01-01 00:00:00Z
// 2018-01-01 00:00:00+08
// 2018-01-01 00:00:00+0800
// 2018-01-01 00:00:00+08:00
// 2018-01-01 00:00:00.000
// 2018-01-01 00:00:00.000Z
// 2018-01-01 00:00:00.000+08
// 2018-01-01 00:00:00.000+0800
// 2018-01-01 00:00:00.000+08:00
// 2018-01-01T00:00:00
// 2018-01-01T00:00:00Z
// 2018-01-01T00:00:00+08
// 2018-01-01T00:00:00+0800
// 2018-01-01T00:00:00+08:00
// 2018-01-01T00:00:00.000
// 2018-01-01T00:00:00.000Z
// 2018-01-01T00:00:00.000+08
// 2018-01-01T00:00:00.000+0800
// 2018-01-01T00:00:00.000+08:00
grails.databinding.dateFormats = [
        "yyyy-MM-dd HH:mm:ss"
        , "yyyy-MM-dd HH:mm:ssX"
        , "yyyy-MM-dd HH:mm:ssXXX"
        , "yyyy-MM-dd HH:mm:ss.SSS"
        , "yyyy-MM-dd HH:mm:ss.SSSX"
        , "yyyy-MM-dd HH:mm:ss.SSSXXX"
        , "yyyy-MM-dd'T'HH:mm:ss"
        , "yyyy-MM-dd'T'HH:mm:ssX"
        , "yyyy-MM-dd'T'HH:mm:ssXXX"
        , "yyyy-MM-dd'T'HH:mm:ss.SSS"
        , "yyyy-MM-dd'T'HH:mm:ss.SSSX"
        , "yyyy-MM-dd'T'HH:mm:ss.SSSXXX"
] + DEFAULT_DATE_FORMATS

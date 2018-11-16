package top.dteam.earth.backend.util

import com.aliyun.oss.OSSClient
import com.aliyun.oss.common.utils.BinaryUtil
import com.aliyun.oss.model.MatchMode
import com.aliyun.oss.model.PolicyConditions
import grails.config.Config
import grails.util.Holders

class AliyunOSSUtil {
    private static final Config CONFIG = Holders.grailsApplication.config
    private static final String ENDPOINT = CONFIG.aliyun?.oss?.endpoint ?: 'oss.aliyuncs.com'
    private static final String ACCESS_KEY_ID = CONFIG.aliyun?.oss?.accessKeyId ?: ''
    private static final String ACCESS_KEY_SECRET = CONFIG.aliyun?.oss?.accessKeySeret ?: ''
    private static final String BUCKET = CONFIG.aliyun?.oss?.bucket ?: ''
    private static final String DIR = CONFIG.aliyun?.oss?.dir ?: ''
    private static final String HOST = "https://${BUCKET}.${ENDPOINT}"
    private static final String CDNURL = CONFIG.aliyun?.oss?.cdnUrl
    private static final OSSClient client = new OSSClient(ENDPOINT, ACCESS_KEY_ID, ACCESS_KEY_SECRET)

    static Map getUploadAuthority(String subDir = '') {
        List<String> dirList = [DIR, subDir]
        dirList.removeAll([null, ''])
        String accessDir = dirList.join('/')
        long expireTime = 300
        long expireEndTime = System.currentTimeMillis() + expireTime * 1000
        Date expiration = new Date(expireEndTime)
        PolicyConditions policyConds = new PolicyConditions()
        policyConds.addConditionItem(PolicyConditions.COND_CONTENT_LENGTH_RANGE, 0, 1048576000)
        policyConds.addConditionItem(MatchMode.StartWith, PolicyConditions.COND_KEY, accessDir)

        String postPolicy = client.generatePostPolicy(expiration, policyConds)
        byte[] binaryData = postPolicy.getBytes("utf-8")
        String encodedPolicy = BinaryUtil.toBase64String(binaryData)
        String postSignature = client.calculatePostSignature(postPolicy)

        [accessKeyId: ACCESS_KEY_ID, policy: encodedPolicy, signature: postSignature
         , dir      : accessDir, host: HOST, expire: expireEndTime, cdnUrl: CDNURL]
    }
}


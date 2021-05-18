package com.xbaimiao.taboolib.gpay;

import com.lly835.bestpay.config.AliPayConfig;
import com.lly835.bestpay.config.WxPayConfig;
import com.lly835.bestpay.enums.BestPayTypeEnum;
import com.lly835.bestpay.model.PayRequest;
import com.lly835.bestpay.model.PayResponse;
import com.lly835.bestpay.service.impl.BestPayServiceImpl;
import com.xbaimiao.taboolib.gpay.utils.Trade;
import io.izzel.taboolib.module.config.TConfig;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

import java.io.File;

public class Pay {

    private static final TConfig tConfig = TConfig.create(new File("src\\main\\resources\\key.yml"));

    public static WxPayConfig wxPayConfig = new WxPayConfig() {{
        ConfigurationSection section = tConfig.getConfigurationSection("pay_wx");
        this.setAppId(section.getString("appid"));
        this.setMchId(section.getString("mchid"));
        this.setMchKey(section.getString("mchKey"));
        this.setAppSecret(section.getString("appSecret"));
        this.setNotifyUrl(section.getString("notifyUrl"));
        this.setKeyPath(section.getString("keyPath"));
/*
默认
        this.setNotifyUrl("http://www.xbaimiao.com/wxpay");
        this.setAppSecret("aefcb056c9a3db39f81d69d3c6ef0dd3");
        this.setMchKey("MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCB");
        this.setMchId("1609554440");
        this.setAppId("wx5e427b4993994769");
        this.setKeyPath("D:\\同步文件夹\\CCPay\\src\\apiclient_cert.p12");
*/
    }};

    public static AliPayConfig aliPayConfig = new AliPayConfig(){{
        this.setAppId("2021002144604411");
        this.setPrivateKey("MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCNaQEmmn/SEHTNMVUSPq2HdMF5U9ZCmbPwi54MIpsljszHp7mG9xI6k896SZCZSculngwQXshambMrx3ofwgioLO2mVun/syHzr4GnLDLZacsrRViEaA3vhexca2rJeRVvJZM8QUe75/6IeTZNRgKNcD83mONii+pWAFqx2wfujsQ2OptBR/PtkGb4WJe4cSto0ZDtZnSan8pIVGxk72tw0IdwSgHXBSdtTpYTIkE2Q5R84s4FQSjK4LwNG704wxz6vtsvu0J8DD39712DmhLjn9J+Az2Lqvvk8wqowBaNRIMeYA+ZOZioxYn6/Q84YFzx26cTASRgi4+ohfkjcHlzAgMBAAECggEAS3j5lkLraZBC5qknwF8Xeq5D/J9UQaOLOB0sM0ndCESY4w6PpIsFfaIaIChXNpj+Sdx6j9omkMRrrjf+5ChJOokQJbFJnqFaw5auWizqzsL6HpQaVEC8/1O4/PKPwgwP9oxVctlckduWK/yVkNSG1DpOYzwEUDpgzZnOzhIav6VDBXR7fPZp2NITuJWXdxyWNR+JPbTxY22ImU+HBCEjdLAZ7kU/giBWqEVrRPKPzECncXf7PBhoGSps9MV1zsjXigVW75VqLytFg7ZjBhj+qRx5J1/8LG9vO6iH44B3h0T/YRId0929Vf1VHKqOC56rOV5KFrVUvPSc2Mz3hIsZkQKBgQDQHdljDGPG+xkSxSht1+COiSSPkUZQTroxDjm7mlDyzabPTSmKZkTv0OU0OupHKVntA0TY19r92GBfypo8D8Stk74k5/TezfiHfaSRY8pED4yaZs3xVR5sc0Q0Ig1rWEaDDRnfm+/46dGZkaxIBvomQ7pTa3/Y/r6MrGI0XjwdfQKBgQCt8h7JVFz9ZQWapY7enoiU5fUQfHlDXqfe8MSh+wZnB4XUi+F3mySY6x5O9Y3SZAiOiAoYsCG0mqCEyHP9WF8hQnXhYcEi2FNaWWK9RywubUuDl+joDvEuzCknj4WbLjnGs5ibLpN99sMGuCgybZHmsvqVphMmNvI46i43uW5lrwKBgF0VzMLZqtM4Qnm7x56B3UXDn5+7acjHvc3tP/NiTWycgUzdhYQKxDDDrZET/O/BViCFwsh+m8vIeSq/UwckQmMk3vpg6YyyccQgdXg2OyUAE3KIIvsbiFUXpVzwEjJ+NGOve3Ahrk8WPhLDLly1CjQNPQzM200yfAYtoGst7xN9AoGBAItdXcU4znhHJEJPOzSqGAwUYlCfKhkMEbvRc/nDwdFyeYtdt3u41Y2fc10X6FUUyLZJ0K0PETI+YTWB1Uy6LR3cvc2iILfmUoIk87Nq/r8D2mjIoVasAhjYgnCr0QQP+UJ2SmKq4okd2AveKHaeyzLnW1XjBZyskU2Acx86dnUJAoGAaJzVulLFofpExRJEX6usLEDnaV7mTfxBf0+oao4TDfq49qpRtn5J8N2JmYEHB1Hw741QxNqcgsX7XePjXG4az2VLsK4NWh1LJmJsiiErUxUtEBxAV6k4TFVn8euNBDj151UBuWTSGClBGtPCZw+nng95IrT893fGJRMBacM3ocg=");
        this.setAliPayPublicKey("MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAl95MgZSjZKSUDcwHJnkv+CpScGfnnx+nCEfLePdXhkgkXkyc9fvjjJfvokFAvP7Bone1AKyppSHk9cYRAugU7NGPfQK4OqHkuEdcqYMhcZDliKapZauV3Um05Zzf5eOwizJZT1nEnwP0A2I2P4Y7XJkBVOTYwAg4wGpLPushYa56vYB19PKoPraSqxX14+zUUtCElRCtS4WtVAu9ovplrCuQ4EL+Z7a2mJw/XdtRAAzgeMXbwLaPNUBXvD5GUYgf3+dcMUVRet1xW7KiR8Cp2Wd0jWLvGMRPJBXf0sv1ewvvzMA8C9yTdbFU92rgLZpniDb94BBDc4CI3ugWkI2wlwIDAQAB");
    }};

    public static BestPayServiceImpl bestPayService = new BestPayServiceImpl();

    static {
        bestPayService.setWxPayConfig(wxPayConfig);
        bestPayService.setAliPayConfig(aliPayConfig);
    }

    public static void main(String[] args) {
        PayRequest payrequest = new PayRequest();
        String orderId = Trade.createOutTradeNo();
        payrequest.setPayTypeEnum(BestPayTypeEnum.ALIPAY_QRCODE);
        payrequest.setOrderId(orderId);
        payrequest.setOrderName("orderName");
        payrequest.setOrderAmount(5.0);
        PayResponse response = bestPayService.pay(payrequest);
        response.setOrderId(orderId);
        response.setOutTradeNo(orderId);
        response.setOrderAmount(5.0);
        System.out.println(response.getCodeUrl());
    }

    public static @NotNull
    PayResponse wxNative(String orderName, Double amount) {
        PayRequest payrequest = new PayRequest();
        String orderId = Trade.createOutTradeNo();
        payrequest.setPayTypeEnum(BestPayTypeEnum.WXPAY_NATIVE);
        payrequest.setOrderId(orderId);
        payrequest.setOrderName(orderName);
        payrequest.setOrderAmount(amount);
        PayResponse response = bestPayService.pay(payrequest);
        response.setOrderId(orderId);
        response.setOutTradeNo(orderId);
        response.setOrderAmount(amount);
        return response;
    }


}

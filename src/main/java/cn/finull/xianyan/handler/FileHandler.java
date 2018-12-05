package cn.finull.xianyan.handler;

import cn.finull.framework.config.AppConfig;
import cn.finull.framework.core.bean.Handler;
import cn.finull.framework.core.request.Parameter;
import cn.finull.framework.core.response.HttpStatus;
import cn.finull.framework.core.response.ResponseEntity;
import cn.finull.xianyan.vo.ImageVO;
import java.util.HashMap;
import java.util.Map;

public class FileHandler implements Handler {
    @Override
    public Class getClassKey() {
        return FileHandler.class;
    }

    /**
     * 文件上传
     * @return 文件名 201：上传成功
     */
    public ResponseEntity<String> upload(Parameter p) {
        String fileName = p.body().byFile("file").upload();
        return new ResponseEntity<>(HttpStatus.CREATED,fileName);
    }

    /**
     * 富文本文件上传
     * @return
     *  {
     *   "code": 0 //0表示成功，其它失败
     *   ,"msg": "" //提示信息 //一般上传失败后返回
     *   ,"data": {
     *     "src": "图片路径"
     *     ,"title": "图片名称" //可选
     *   }
     * }
     */
    public ResponseEntity<ImageVO> editUpload(Parameter p) {
        String fileName = p.body().byFile("file").upload();
        ImageVO imageVO = new ImageVO();
        imageVO.setCode(0);
        imageVO.setMsg("success");
        Map<String,String> data = new HashMap<>();
        data.put("src",AppConfig.getHttpPrefix() + fileName);
        data.put("title",fileName);
        imageVO.setData(data);
        return new ResponseEntity<>(imageVO);
    }
}

package com.myseckill.seckill.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**公共返回对象
 * @author HCW
 * @date 2020/12/30-9:02
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResponBean {
    private long code;
    private String message;
    private Object obj;

    /**
     * 执行成功返回返回一个ResponBean对象,obj=null
     * @return
     */
    public static ResponBean success(){
        return new ResponBean(ResponBeanEnum.SUCCESS.getCode(),ResponBeanEnum.SUCCESS.getMessage(),null);
    }

    /**
     * 执行成功返回ResponBean对象带有Object对象
     * @param object
     * @return
     */
    public static ResponBean success(Object object){
        return  new ResponBean(ResponBeanEnum.SUCCESS.getCode(),ResponBeanEnum.SUCCESS.getMessage(),object);

    }

    /**
     * 执行失败，返回一个错误的ResponBean对象
     * @return
     */
    public static ResponBean error(ResponBeanEnum responBeanEnum){
        return  new ResponBean(responBeanEnum.getCode(),responBeanEnum.getMessage(),null);
    }
}

package mybatis.core.entity;

import java.io.Serializable;

/**
 * 查询参数
 *
 * @author lgt
 * @date 2019/5/2 : 11:04 AM
 */
public class ParamCondition implements Serializable {


    private ParamSymbol paramSymbol;

    private String paramName;

    private String fieldName;

    public ParamCondition(int criteriaIdxint, int paramIndex, String fieldName, ParamSymbol paramSymbol) {
        this.paramSymbol = paramSymbol;
        this.paramName = criteriaIdxint + "_" + fieldName + "_" + paramIndex;
        this.fieldName = fieldName;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public ParamSymbol getParamSymbol() {
        return paramSymbol;
    }

    public void setParamSymbol(ParamSymbol paramSymbol) {
        this.paramSymbol = paramSymbol;
    }


    public String getParamPlaceName() {
        return ParamConstant.CONDITION + "." + ParamConstant.PARAMS + "." + getParamName();
    }

    public String getParamName() {
        return paramName;
    }

    public void setParamName(String paramName) {
        this.paramName = paramName;
    }
}

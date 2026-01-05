package ${packageUrl};

<#if swaggerEnable == true>
<#if apiDocType == "swagger">
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
<#elseif apiDocType == "openapi">
import io.swagger.v3.oas.annotations.media.Schema;
</#if>
import lombok.Data;
</#if>
<#list importPackages as package>
import ${package};
</#list>

/**
 * @说明: ${tableComment}接口
 * @作者: ${author} powered By noWork.fun
 * @创建时间: ${curTime}
 */
@Data
<#if swaggerEnable == true>
<#if apiDocType == "swagger">
@ApiModel("${tableComment}")
<#elseif apiDocType == "openapi">
@Schema(name = "${tableComment}")
</#if>
</#if>
public class ${entityName}Dto {

<#list columnInfos as col>
    <#if swaggerEnable == true>
    <#if apiDocType == "swagger">
    @ApiModelProperty(value = "${col.columnComment}")
    <#elseif apiDocType == "openapi">
    @Schema(description = "${col.columnComment}")
    </#if>
    <#else>
    /**
    * ${col.columnComment}")
    */
    </#if>
    private ${col.javaType} ${col.javaName};

</#list>

}
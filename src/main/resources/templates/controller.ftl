package ${packageUrl};


import com.github.pagehelper.PageInfo;
import ${entityUrl}.${entityName};
import ${dtoUrl}.${entityName}Dto;
import ${serviceUrl}.${entityName}Service;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.*;
<#-- 自定义返回对象 -->
<#if customProperties?? && customProperties.returnObject??>
import ${customProperties.returnObject.packagePath};
<#else>
import ${packageUrl}.Result;
import ${packageUrl}.BaseResponseDto;
</#if>
<#if swaggerEnable == true>
<#if apiDocType == "swagger">
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiOperation;
<#elseif apiDocType == "openapi">
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
</#if>
</#if>

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @说明: ${tableComment}相关接口
 * @作者: ${author}
 * @创建时间: ${curTime}
 */
@RestController
<#if swaggerEnable == true>
<#if apiDocType == "swagger">
@Api(tags = {"${tableComment}相关接口"})
<#elseif apiDocType == "openapi">
@Tag(name = "${tableComment}相关接口")
</#if>
</#if>
@RequestMapping("/api/${entityStartByLowCase}")
public class ${entityName}Controller {

    private final ${entityName}Service ${entityStartByLowCase}Service;

    public ${entityName}Controller(${entityName}Service ${entityStartByLowCase}Service) {
        this.${entityStartByLowCase}Service = ${entityStartByLowCase}Service;
    }

    /**
     * 查询列表
     */
    <#if swaggerEnable == true>
    <#if apiDocType == "swagger">
    @ApiOperation("查询列表")
    <#elseif apiDocType == "openapi">
    @Operation(summary = "查询列表")
    </#if>
    </#if>
    @PostMapping("/list")
    public BaseResponseDto<List<${entityName}Dto>> list(@RequestBody @Nullable ${entityName}Dto ${entityStartByLowCase}) {
        <#-- 使用DTO作为请求对象 -->
        List<${entityName}> list = ${entityStartByLowCase}Service.list(<#if entityStartByLowCase??>${entityStartByLowCase}.toEntity()<#else>null</#if>);
        List<${entityName}Dto> resList = list.stream().map(${entityName}::toDto).collect(Collectors.toList());
        return Result.success("查询成功", resList);
    }

    /**
     * 通过id查询对象
     * TODO 这里记录不存在需要使用异常处理
     */
    <#if swaggerEnable == true>
    <#if apiDocType == "swagger">
    @ApiOperation("通过id查询对象")
    <#elseif apiDocType == "openapi">
    @Operation(summary = "通过id查询对象")
    </#if>
    </#if>
    @GetMapping("/selById/{id}")
    public BaseResponseDto<${entityName}Dto> selById(<#if swaggerEnable == true><#if apiDocType == "swagger">@ApiParam(name = "id", value = "需要查询数据的id")<#elseif apiDocType == "openapi">@Parameter(name = "id", description = "需要查询数据的id")</#if></#if> @PathVariable Long id) {
        ${entityName} ${entityStartByLowCase} = ${entityStartByLowCase}Service.selById(id);
        if (Objects.nonNull(${entityStartByLowCase})){
            return Result.success("查询成功",${entityStartByLowCase}.toDto());
        }
        return Result.fail("记录不存在");
    }

    /**
     * 新增
     */
    <#if swaggerEnable == true>
    <#if apiDocType == "swagger">
    @ApiOperation("新增")
    <#elseif apiDocType == "openapi">
    @Operation(summary = "新增")
    </#if>
    </#if>
    @PostMapping("/add")
    public BaseResponseDto<String> add(@RequestBody ${entityName}Dto ${entityStartByLowCase}) {
        <#-- 使用DTO作为请求对象 -->
        Integer res = ${entityStartByLowCase}Service.add(${entityStartByLowCase}.toEntity());
        if (res == 1){
            return Result.success("新增成功");
        }
        return Result.fail("新增失败");
    }

    /**
     * 更新
     */
    <#if swaggerEnable == true>
    <#if apiDocType == "swagger">
    @ApiOperation("更新")
    <#elseif apiDocType == "openapi">
    @Operation(summary = "更新")
    </#if>
    </#if>
    @PutMapping("/update")
    public BaseResponseDto<String> update(@RequestBody ${entityName}Dto ${entityStartByLowCase}) {
        <#-- 使用DTO作为请求对象 -->
        ${entityName} selById = ${entityStartByLowCase}Service.selById(${entityStartByLowCase}.getId());
        if (Objects.isNull(selById)) {
            return Result.fail("记录不存在");
        }
        Integer res = ${entityStartByLowCase}Service.update(${entityStartByLowCase}.toEntity());
        if (res == 1) {
            return Result.success("更新成功");
        }
        return Result.fail("更新失败");
    }

    /**
     * 通过id删除
     */
    <#if swaggerEnable == true>
    <#if apiDocType == "swagger">
    @ApiOperation("通过id删除")
    <#elseif apiDocType == "openapi">
    @Operation(summary = "通过id删除")
    </#if>
    </#if>
    @PutMapping("/delById/{id}")
    public BaseResponseDto<String> delById(<#if swaggerEnable == true><#if apiDocType == "swagger">@ApiParam(name = "id", value = "需要删除数据的id")<#elseif apiDocType == "openapi">@Parameter(name = "id", description = "需要删除数据的id")</#if></#if> @PathVariable Long id) {
        Integer res = ${entityStartByLowCase}Service.delById(id);
        if (res == 1){
            return Result.success("删除成功");
        }
        return Result.fail("删除失败");
    }

    /**
     * 批量删除
     */
    <#if swaggerEnable == true>
    <#if apiDocType == "swagger">
    @ApiOperation("批量删除")
    <#elseif apiDocType == "openapi">
    @Operation(summary = "批量删除")
    </#if>
    </#if>
    @PutMapping("/delBatchByIdList")
    public BaseResponseDto<String> delBatchByIdList(@RequestBody List<Long> ids) {
        if (ids.size() <= 0) {
            return Result.fail("参数错误");
        }
        Integer res = ${entityStartByLowCase}Service.delBatchByIdList(ids);
        if (res == ids.size()) {
            return Result.success("删除成功");
        }
        return Result.fail("删除成功/部分记录不存在");
    }

    /**
     * 分页列表查询
     */
    <#if swaggerEnable == true>
    <#if apiDocType == "swagger">
    @ApiOperation("分页列表查询")
    <#elseif apiDocType == "openapi">
    @Operation(summary = "分页列表查询")
    </#if>
    </#if>
    @PostMapping("/page/{pageNum}/{pageSize}")
    public BaseResponseDto<PageInfo<${entityName}Dto>> page(@RequestBody @Nullable ${entityName}Dto ${entityStartByLowCase},<#if swaggerEnable == true><#if apiDocType == "swagger">@ApiParam(name = "pageNum", value = "页码")<#elseif apiDocType == "openapi">@Parameter(name = "pageNum", description = "页码")</#if></#if> @PathVariable int pageNum, <#if swaggerEnable == true><#if apiDocType == "swagger">@ApiParam(name = "pageSize", value = "每页数量")<#elseif apiDocType == "openapi">@Parameter(name = "pageSize", description = "每页数量")</#if></#if> @PathVariable int pageSize) {
        <#-- 使用DTO作为请求对象，并返回DTO列表 -->
        PageInfo<${entityName}> page = ${entityStartByLowCase}Service.page(<#if entityStartByLowCase??>${entityStartByLowCase}.toEntity()<#else>null</#if>, pageNum, pageSize);
        PageInfo<${entityName}Dto> dtoPage = new PageInfo<>();
        dtoPage.setPageNum(page.getPageNum());
        dtoPage.setPageSize(page.getPageSize());
        dtoPage.setTotal(page.getTotal());
        dtoPage.setList(page.getList().stream().map(${entityName}::toDto).collect(Collectors.toList()));
        return Result.success("查询成功", dtoPage);
    }

}
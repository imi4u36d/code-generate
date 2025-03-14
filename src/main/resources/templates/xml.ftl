<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd" >
<mapper namespace="${mapperUrl}.${entityName}Mapper">

    <resultMap type="${entityUrl}.${entityName}" id="${entityName}Result">
        <#list columnInfos as col>
            <result column="${col.columnName}"   property="${col.javaName}" />
        </#list>
    </resultMap>

    <sql id="sel${entityStartByLowCase}Vo">
        <#list columnInfos as col>
            ${col.columnName}<#if col_has_next>,</#if>
        </#list>
    </sql>

    <select id="list" parameterType="${entityUrl}.${entityName}" resultMap="${entityName}Result">
        select
        <include refid="sel${entityStartByLowCase}Vo"/>
        from ${tableName}
        <where>
            <#list columnInfos as col>
                <if test="${col.javaName} != null> and ${col.columnName} = <#noparse>#</#noparse>{${col.javaName}}</if>
            </#list>
        </where>
        order by id desc
    </select>

    <select id="totalSize" resultType="java.lang.Long">
        select count(*) from ${tableName}
        <where>
            <#list columnInfos as col>
                <if test="${col.javaName} != null> and ${col.columnName} = <#noparse>#</#noparse>{${col.javaName}}</if>
            </#list>
        </where>
    </select>

    <select id="selById" parameterType="Long" resultMap="${entityName}Result">
        <include refid="sel${entityStartByLowCase}Vo"/>
        where id = <#noparse>#</#noparse>{id}
    </select>

    <insert id="add" parameterType="${entityUrl}.${entityName}">
        insert into ${tableName}
        <trim prefix="(" suffix=")" suffixOverrides=",">
            <#list columnInfos as col>
                <if test="${col.javaName} != null">${col.columnName},</if>
            </#list>
        </trim>
        <trim prefix="values (" suffix=")" suffixOverrides=",">
            <#list columnInfos as col>
                <if test="${col.javaName} != null"><#noparse>#</#noparse>{${col.javaName}},</if>
            </#list>
        </trim>
    </insert>

    <update id="update" parameterType="${entityUrl}.${entityName}">
        update ${tableName}
        <trim prefix="SET" suffixOverrides=",">
            <#list columnInfos as col>
                <if test="${col.javaName} != null">${col.columnName} = <#noparse>#</#noparse>{${col.javaName}},</if>
            </#list>
        </trim>
        where id = <#noparse>#</#noparse>{id}
    </update>

    <delete id="delById" parameterType="Long">
        delete from ${tableName} where id = <#noparse>#</#noparse>{id}
    </delete>

    <delete id="delBatchByIdList" parameterType="Long">
        delete from ${tableName} where id in
        <foreach item="id" collection="array" open="(" separator="," close=")">
            <#noparse>#</#noparse>{id}
        </foreach>
    </delete>

    <select id="total" parameterType="${entityUrl}.${entityName}" resultType="Integer">
        select count(*) from ${tableName}
        <where>
            <#list columnInfos as col>
                <if test="${col.javaName} != null> and ${col.columnName} = <#noparse>#</#noparse>{${col.javaName}}</if>
            </#list>
        </where>
    </select>

</mapper>
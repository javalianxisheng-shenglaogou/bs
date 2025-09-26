package com.multisite.cms.dto.site;

import com.multisite.cms.enums.SiteStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 站点创建请求DTO
 * 
 * @author 姚奇奇
 * @version 1.0.0
 * @since 2024-01-01
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "站点创建请求")
public class SiteCreateRequest {

    /**
     * 站点名称
     */
    @Schema(description = "站点名称", example = "示例站点", required = true)
    @NotBlank(message = "站点名称不能为空")
    @Size(min = 2, max = 100, message = "站点名称长度必须在2-100个字符之间")
    private String name;

    /**
     * 站点代码
     */
    @Schema(description = "站点代码", example = "example_site", required = true)
    @NotBlank(message = "站点代码不能为空")
    @Size(min = 2, max = 50, message = "站点代码长度必须在2-50个字符之间")
    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "站点代码只能包含字母、数字和下划线")
    private String code;

    /**
     * 域名
     */
    @Schema(description = "域名", example = "example.com")
    @Size(max = 100, message = "域名长度不能超过100个字符")
    @Pattern(regexp = "^[a-zA-Z0-9.-]+$", message = "域名格式不正确")
    private String domain;

    /**
     * 站点描述
     */
    @Schema(description = "站点描述", example = "这是一个示例站点")
    @Size(max = 500, message = "站点描述长度不能超过500个字符")
    private String description;

    /**
     * 站点状态
     */
    @Schema(description = "站点状态", example = "ACTIVE")
    private SiteStatus status = SiteStatus.ACTIVE;

    /**
     * 排序值
     */
    @Schema(description = "排序值", example = "0")
    private Integer sortOrder = 0;

    /**
     * 父站点ID
     */
    @Schema(description = "父站点ID", example = "1")
    private Long parentSiteId;

    /**
     * 所有者ID
     */
    @Schema(description = "所有者ID", example = "1")
    private Long ownerId;
}

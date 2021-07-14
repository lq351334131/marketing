package marketing.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @Author chengrong.yang
 * @Date 2021-03-04 10:55:40
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class RequestId implements Serializable {

    private static final long serialVersionUID = -2865042898374806226L;

    @ApiModelProperty(value = "ID", required = true)
    @NotNull(message = "id不能为空")
    private Long id;

}
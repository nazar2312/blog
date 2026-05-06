package com.portfolio.blog.domain.dto.payment;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ProductResponse {

    private String name;
    private String id;
    private boolean active;

}

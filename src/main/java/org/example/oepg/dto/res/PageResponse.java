package org.example.oepg.dto.res;

import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 分页响应 DTO
 * 通用分页数据封装
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageResponse<T> {
    
    /**
     * 当前页数据
     */
    private List<T> records;
    
    /**
     * 总记录数
     */
    private Long total;
    
    /**
     * 每页大小
     */
    private Long size;
    
    /**
     * 当前页码
     */
    private Long current;
    
    /**
     * 总页数
     */
    private Long pages;
    
    /**
     * 是否有上一页
     */
    private Boolean hasPrevious;
    
    /**
     * 是否有下一页
     */
    private Boolean hasNext;
    
    /**
     * 从 MyBatis Plus 的 IPage 创建 PageResponse
     */
    public static <T> PageResponse<T> fromIPage(IPage<T> page) {
        PageResponse<T> response = new PageResponse<>();
        response.setRecords(page.getRecords());
        response.setTotal(page.getTotal());
        response.setSize(page.getSize());
        response.setCurrent(page.getCurrent());
        response.setPages(page.getPages());
        response.setHasPrevious(page.getCurrent() > 1);
        response.setHasNext(page.getCurrent() < page.getPages());
        return response;
    }
    
    /**
     * 创建空的分页响应
     */
    public static <T> PageResponse<T> empty() {
        return new PageResponse<>(java.util.Collections.emptyList(), 0L, 10L, 1L, 0L, false, false);
    }
}

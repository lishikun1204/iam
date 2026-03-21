package com.iam.common.api;

import java.util.List;

public class PageDto<T> {
  private List<T> items;
  private int page;
  private int pageSize;
  private long total;

  public PageDto() {
  }

  public PageDto(final List<T> items, final int page, final int pageSize, final long total) {
    this.items = items;
    this.page = page;
    this.pageSize = pageSize;
    this.total = total;
  }

  public List<T> getItems() {
    return items;
  }

  public void setItems(final List<T> items) {
    this.items = items;
  }

  public int getPage() {
    return page;
  }

  public void setPage(final int page) {
    this.page = page;
  }

  public int getPageSize() {
    return pageSize;
  }

  public void setPageSize(final int pageSize) {
    this.pageSize = pageSize;
  }

  public long getTotal() {
    return total;
  }

  public void setTotal(final long total) {
    this.total = total;
  }
}


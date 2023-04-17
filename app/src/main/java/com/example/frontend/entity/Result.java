package com.example.frontend.entity;

import java.util.List;

public class Result {
    private Boolean success;
    private String errorMsg;
    private Object data;
    private Long total;

    public static Result ok() {
        return new Result(true, (String) null, (Object) null, (Long) null);
    }

    public static Result ok(Object data) {
        return new Result(true, (String) null, data, (Long) null);
    }

    public static Result ok(List<?> data, Long total) {
        return new Result(true, (String) null, data, total);
    }

    public static Result fail(String errorMsg) {
        return new Result(false, errorMsg, (Object) null, (Long) null);
    }

    public Boolean getSuccess() {
        return this.success;
    }

    public String getErrorMsg() {
        return this.errorMsg;
    }

    public Object getData() {
        return this.data;
    }

    public Long getTotal() {
        return this.total;
    }

    public void setSuccess(final Boolean success) {
        this.success = success;
    }

    public void setErrorMsg(final String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public void setData(final Object data) {
        this.data = data;
    }

    public void setTotal(final Long total) {
        this.total = total;
    }

    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof Result)) {
            return false;
        } else {
            Result other = (Result) o;
            if (!other.canEqual(this)) {
                return false;
            } else {
                label59:
                {
                    Object this$success = this.getSuccess();
                    Object other$success = other.getSuccess();
                    if (this$success == null) {
                        if (other$success == null) {
                            break label59;
                        }
                    } else if (this$success.equals(other$success)) {
                        break label59;
                    }

                    return false;
                }

                Object this$total = this.getTotal();
                Object other$total = other.getTotal();
                if (this$total == null) {
                    if (other$total != null) {
                        return false;
                    }
                } else if (!this$total.equals(other$total)) {
                    return false;
                }

                Object this$errorMsg = this.getErrorMsg();
                Object other$errorMsg = other.getErrorMsg();
                if (this$errorMsg == null) {
                    if (other$errorMsg != null) {
                        return false;
                    }
                } else if (!this$errorMsg.equals(other$errorMsg)) {
                    return false;
                }

                Object this$data = this.getData();
                Object other$data = other.getData();
                if (this$data == null) {
                    return other$data == null;
                } else return this$data.equals(other$data);
            }
        }
    }

    protected boolean canEqual(final Object other) {
        return other instanceof Result;
    }

    public int hashCode() {
        boolean PRIME = true;
        int result = 1;
        Object $success = this.getSuccess();
        result = result * 59 + ($success == null ? 43 : $success.hashCode());
        Object $total = this.getTotal();
        result = result * 59 + ($total == null ? 43 : $total.hashCode());
        Object $errorMsg = this.getErrorMsg();
        result = result * 59 + ($errorMsg == null ? 43 : $errorMsg.hashCode());
        Object $data = this.getData();
        result = result * 59 + ($data == null ? 43 : $data.hashCode());
        return result;
    }

    public String toString() {
        return "Result(success=" + this.getSuccess() + ", errorMsg=" + this.getErrorMsg() + ", data=" + this.getData() + ", total=" + this.getTotal() + ")";
    }

    public Result() {
    }

    public Result(final Boolean success, final String errorMsg, final Object data, final Long total) {
        this.success = success;
        this.errorMsg = errorMsg;
        this.data = data;
        this.total = total;
    }
}

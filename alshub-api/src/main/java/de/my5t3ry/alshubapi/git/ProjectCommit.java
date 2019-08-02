package de.my5t3ry.alshubapi.git;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ProjectCommit {
    private final String msg;
    private final String time;
    private final String id;
    private  boolean checkedOut = false;

    public String getMsg() {
        return this.msg;
    }

    public String getTime() {
        return this.time;
    }

    public String getId() {
        return this.id;
    }

    public boolean isCheckedOut() {
        return this.checkedOut;
    }

    public void setCheckedOut(boolean checkedOut) {
        this.checkedOut = checkedOut;
    }

    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof ProjectCommit)) return false;
        final ProjectCommit other = (ProjectCommit) o;
        if (!other.canEqual((Object) this)) return false;
        final Object this$msg = this.getMsg();
        final Object other$msg = other.getMsg();
        if (this$msg == null ? other$msg != null : !this$msg.equals(other$msg)) return false;
        final Object this$time = this.getTime();
        final Object other$time = other.getTime();
        if (this$time == null ? other$time != null : !this$time.equals(other$time)) return false;
        final Object this$id = this.getId();
        final Object other$id = other.getId();
        if (this$id == null ? other$id != null : !this$id.equals(other$id)) return false;
        if (this.isCheckedOut() != other.isCheckedOut()) return false;
        return true;
    }

    protected boolean canEqual(final Object other) {
        return other instanceof ProjectCommit;
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $msg = this.getMsg();
        result = result * PRIME + ($msg == null ? 43 : $msg.hashCode());
        final Object $time = this.getTime();
        result = result * PRIME + ($time == null ? 43 : $time.hashCode());
        final Object $id = this.getId();
        result = result * PRIME + ($id == null ? 43 : $id.hashCode());
        result = result * PRIME + (this.isCheckedOut() ? 79 : 97);
        return result;
    }

    public String toString() {
        return "ProjectCommit(msg=" + this.getMsg() + ", time=" + this.getTime() + ", id=" + this.getId() + ", checkedOut=" + this.isCheckedOut() + ")";
    }
}

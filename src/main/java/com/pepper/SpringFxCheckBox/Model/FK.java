package com.pepper.SpringFxCheckBox.Model;


public class FK 
{
    private String requestedTable;
    private String rtForeignKey;
    private String referencedTable;
    private String referencedFK;

    public FK(String requestedTable, String rtForeignKey, String referencedTable, String referencedFK) {
        this.requestedTable = requestedTable;
        this.rtForeignKey = rtForeignKey;
        this.referencedTable = referencedTable;
        this.referencedFK = referencedFK;
    }

    public String getRequestedTable() {
        return requestedTable;
    }

    public String getRtForeignKey() {
        return rtForeignKey;
    }

    public String getReferencedTable() {
        return referencedTable;
    }

    public String getReferencedFK() {
        return referencedFK;
    }

    public void setRequestedTable(String requestedTable) {
        this.requestedTable = requestedTable;
    }

    public void setRtForeignKey(String rtForeignKey) {
        this.rtForeignKey = rtForeignKey;
    }

    public void setReferencedTable(String referencedTable) {
        this.referencedTable = referencedTable;
    }

    public void setReferencedFK(String referencedFK) {
        this.referencedFK = referencedFK;
    }
    
    

}

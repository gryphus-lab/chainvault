/*
 * Copyright (c) 2026. Gryphus Lab
 */
import {
  CCol,
  CRow,
  CTable,
  CTableBody,
  CTableDataCell,
  CTableHead,
  CTableHeaderCell,
  CTableRow,
  CWidgetStatsB,
} from '@coreui/react'

const Dashboard = () => {
  return (
    <>
      <CRow>
        {/* TODO: Populate with actual data from getMigrationStats */}
        <CCol xs={6}>
          <CWidgetStatsB className="mb-3" color="primary" title="Total" value="0" />
        </CCol>
        <CCol xs={6}>
          <CWidgetStatsB className="mb-3" color="secondary" title="In Progress" value="0" />
        </CCol>
        <CCol xs={6}>
          <CWidgetStatsB className="mb-3" color="success" title="Success" value="0" />
        </CCol>
        <CCol xs={6}>
          <CWidgetStatsB className="mb-3" color="danger" title="Error" value="0" />
        </CCol>
      </CRow>
      <CTable striped>
        <CTableHead>
          <CTableRow>
            <CTableHeaderCell scope="col">#</CTableHeaderCell>
            <CTableHeaderCell scope="col">DocId</CTableHeaderCell>
            <CTableHeaderCell scope="col">Title</CTableHeaderCell>
            <CTableHeaderCell scope="col">Status</CTableHeaderCell>
            <CTableHeaderCell scope="col">Created At</CTableHeaderCell>
            <CTableHeaderCell scope="col">Updated At</CTableHeaderCell>
            <CTableHeaderCell scope="col">View Details</CTableHeaderCell>
          </CTableRow>
        </CTableHead>
        <CTableBody>
          {/* TODO: Populate with actual data from getMigrations */}
          <CTableRow>
            <CTableDataCell colSpan={7} className="text-center text-muted py-4">
              No documents available
            </CTableDataCell>
          </CTableRow>
        </CTableBody>
      </CTable>
    </>
  )
}

export default Dashboard

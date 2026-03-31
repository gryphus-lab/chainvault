/*
 * Copyright (c) 2026. Gryphus Lab
 */
import { Box, useTheme } from "@mui/material";
import { DataGrid } from "@mui/x-data-grid";
import { tokens } from "@/theme";

// eslint-disable-next-line @typescript-eslint/no-explicit-any
function MigrationDataGrid(data: any) {
  console.log(data);
  const theme = useTheme();
  const colors = tokens(theme.palette.mode);

  const columns = [
    { field: "id", headerName: "Migration ID", flex: 0.5 },
    {
      field: "status",
      headerName: "Status",
      flex: 1,
      cellClassName: "name-column--cell",
    },
    { field: "title", headerName: "Title" },
    { field: "createdAt", headerName: "Created" },
    { field: "updatedAt", headerName: "Updated" },
    { field: "Actions", headerName: "Actions" },
  ];

  return (
    <Box
      height="75vh"
      maxWidth="100%"
      gridColumn="span 12"
      mb="10px"
      sx={{
        "& .MuiDataGrid-root": {
          border: "none",
        },
        "& .MuiDataGrid-cell": {
          border: "none",
        },
        "& .name-column--cell": {
          color: colors.greenAccent[300],
        },
        "& .MuiDataGrid-columnHeaders": {
          backgroundColor: colors.blueAccent[700],
          borderBottom: "none",
        },
        "& .MuiDataGrid-virtualScroller": {
          backgroundColor: colors.primary[400],
        },
        "& .MuiDataGrid-footerContainer": {
          borderTop: "none",
          backgroundColor: colors.blueAccent[700],
        },
        "& .MuiCheckbox-root": {
          color: `${colors.greenAccent[200]} !important`,
        },
        "& .MuiDataGrid-iconSeparator": {
          color: colors.primary[100],
        },
        "& .MuiDataGrid-toolbarContainer .MuiButton-text": {
          color: `${colors.gray[100]} !important`,
        },
        "& .css-1uh4g4p .MuiDataGrid-columnHeaderTitle": {
          color: `${colors.primary[100]} !important`,
        },
      }}
    >
      <Box mb="10px"></Box>
      <DataGrid
        rows={data}
        columns={columns}
        //   components={{ Toolbar: GridToolbar }}
        initialState={{
          pagination: {
            paginationModel: {
              pageSize: 10,
            },
          },
        }}
        //   checkboxSelection
      />
    </Box>
  );
}

export default MigrationDataGrid;

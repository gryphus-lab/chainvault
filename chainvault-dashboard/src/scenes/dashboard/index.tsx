/*
 * Copyright (c) 2026. Gryphus Lab
 */
import { Box, useMediaQuery, useTheme } from "@mui/material";

import { Header } from "@/components";
import { tokens } from "@/theme";
import { Route, Routes } from "react-router-dom";
import Overview from "@/pages/Overview";
import MigrationDetailPage from "@/pages/MigrationDetailPage";
import AvgEngineLoadsFleetFuelLevel from "@/scenes/dashboard/avgEngineLoadsFleetFuelLevel.tsx";

function Dashboard() {
  const theme = useTheme();
  const colors = tokens(theme.palette.mode);
  const isXlDevices = useMediaQuery("(min-width: 1260px)");
  const isMdDevices = useMediaQuery("(min-width: 724px)");
  const isXsDevices = useMediaQuery("(max-width: 436px)");

  return (
    <Box m="20px">
      <Box display="flex" justifyContent="space-between" alignItems="center">
        <Header title="Chainvault" subtitle="Migration Dashboard" />
        {!isXsDevices && (
          <Box mt="20px" display="flex" alignItems="center" gap={1}></Box>
        )}
      </Box>

      <Box
        display="grid"
        gridTemplateColumns={
          isXlDevices
            ? "repeat(12, 1fr)"
            : isMdDevices
              ? "repeat(6, 1fr)"
              : "repeat(3, 1fr)"
        }
        gridAutoRows="140px"
        gap="20px"
      >
        <AvgEngineLoadsFleetFuelLevel
          colors={colors}
          selectedData={undefined}
        />
      </Box>
      <Box sx={{ flex: 1 }}>
        <Routes>
          <Route path="/" element={<Overview />} />
          <Route path="/migration/:id" element={<MigrationDetailPage />} />
        </Routes>
      </Box>
    </Box>
  );
}

export default Dashboard;

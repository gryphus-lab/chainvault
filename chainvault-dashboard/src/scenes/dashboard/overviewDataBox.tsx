/*
 * Copyright (c) 2026. Gryphus Lab
 */
import { Box } from "@mui/material";
import { StatBox } from "@/components";
import PowerIcon from "@mui/icons-material/Power";
import LocalGasStationIcon from "@mui/icons-material/LocalGasStation";
import ElectricalServicesIcon from "@mui/icons-material/ElectricalServices";
import WarningAmberIcon from "@mui/icons-material/WarningAmber";

const OverviewDataBox = ({ colors, stats }) => {
  return (
    <>
      <Box
        gridColumn="span 3"
        bgcolor={colors.primary[400]}
        display="flex"
        alignItems="center"
        justifyContent="center"
        sx={{ cursor: "pointer" }}
      >
        <StatBox
          title={stats?.total ?? 0}
          subtitle="Total Migrations"
          //   progress="0.75"
          //   increase="+14%"
          icon={
            <PowerIcon
              sx={{ color: colors.greenAccent[600], fontSize: "26px" }}
            />
          }
        />
      </Box>
      <Box
        gridColumn="span 3"
        backgroundColor={colors.primary[400]}
        display="flex"
        alignItems="center"
        justifyContent="center"
        sx={{ cursor: "pointer" }}
      >
        <StatBox
          title={stats?.success ?? 0}
          subtitle="Successful"
          //   progress="0.50"
          //   increase="+21%"
          icon={
            <LocalGasStationIcon
              sx={{ color: colors.greenAccent[600], fontSize: "26px" }}
            />
          }
        />
      </Box>
      <Box
        gridColumn="span 3"
        backgroundColor={colors.primary[400]}
        display="flex"
        alignItems="center"
        justifyContent="center"
        sx={{ cursor: "pointer" }}
      >
        <StatBox
          title={(stats?.pending ?? 0) + (stats?.running ?? 0)}
          subtitle="In Progress"
          //   progress="0.30"
          //   increase="+5%"
          icon={
            <ElectricalServicesIcon
              sx={{ color: colors.greenAccent[600], fontSize: "26px" }}
            />
          }
        />
      </Box>
      <Box
        gridColumn="span 3"
        backgroundColor={colors.primary[400]}
        display="flex"
        alignItems="center"
        justifyContent="center"
        sx={{ cursor: "pointer" }}
      >
        <StatBox
          title={stats?.failed ?? 0}
          subtitle="Failed"
          //   progress="0.80"
          //   increase="+43%"
          icon={
            <WarningAmberIcon
              sx={{ color: colors.redAccent[600], fontSize: "26px" }}
            />
          }
        />
      </Box>
    </>
  );
};

export default OverviewDataBox;

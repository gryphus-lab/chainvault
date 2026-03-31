/*
 * Copyright (c) 2026. Gryphus Lab
 */
import { Box } from "@mui/material";
import Plot from "react-plotly.js";
import Chart from "react-apexcharts";
import AvgEngineLoadsChart from "./avgEngineLoadsChart";
import FleetFuelLevelChart from "./fleetFuelLevelChart";
import OverviewDataBox from "@/scenes/dashboard/overviewDataBox";
import { getMigrationStats } from "@/lib/api";
import { useQuery } from "@tanstack/react-query";

const StatisticsPanel = ({ colors, selectedData }) => {
  const ApexGaugeChart = ({ title, value }) => {
    const getApexColor = (val: number) => {
      if (val <= 80) return "#4ade80";
      if (val <= 90) return "#facc15";
      return "#f87171";
    };

    const fillColor = getApexColor(value);

    const options = {
      chart: { type: "radialBar", sparkline: { enabled: true } },
      plotOptions: {
        radialBar: {
          startAngle: -135,
          endAngle: 135,
          hollow: { size: "60%" },
          track: {
            background: "#e0e0e0",
            strokeWidth: "100%",
          },
          dataLabels: {
            name: {
              fontSize: "14px",
              offsetY: 30,
            },
            value: {
              fontSize: "24px",
              offsetY: -10,
              formatter: (val) => val + "%",
              color: fillColor,
            },
          },
        },
      },
      labels: [title],
      fill: {
        colors: [fillColor],
      },
    };

    return (
      <Chart
        options={options}
        series={[value]}
        type="radialBar"
        height={400}
        width={350}
      />
    );
  };

  const PlotlyGaugeChart = ({ title, value }) => (
    <Plot
      data={[
        {
          type: "indicator",
          mode: "gauge+number",
          value: value,
          gauge: {
            axis: {
              range: [0, 100],
              tickmode: "array",
              tickvals: [0, 20, 40, 60, 80, 100],
            },
            bar: { color: "#2f3e46" },
            steps: [
              { range: [0, 80], color: "#4ade80" }, // green
              { range: [80, 90], color: "#facc15" }, // yellow
              { range: [90, 100], color: "#f87171" }, // red
            ],
          },
          number: { suffix: "%" },
        },
      ]}
      layout={{
        width: 350,
        height: 400,
        margin: { t: 0, b: 0, l: 10, r: 10 },
        title: { text: title, font: { size: 16 } },
      }}
    />
  );

  const { data: stats } = useQuery({
    queryKey: ["migration-stats"],
    queryFn: getMigrationStats,
  });

  return (
    <>
      <Box
        gridColumn="span 4"
        gridRow="span 2"
        backgroundColor={colors.primary[400]}
        p="30px"
        sx={{ cursor: "pointer" }}
      >
        <Box
          display="flex"
          alignItems="center"
          justifyContent="center"
          height="250px"
          mt="-20px"
        >
          <AvgEngineLoadsChart
            ApexGaugeChart={ApexGaugeChart}
            PlotlyGaugeChart={PlotlyGaugeChart}
            selectedData={selectedData}
          />
        </Box>
      </Box>

      <Box
        gridColumn="span 4"
        gridRow="span 2"
        backgroundColor={colors.primary[400]}
        p="30px"
        sx={{ cursor: "pointer" }}
      >
        <Box
          display="flex"
          alignItems="center"
          justifyContent="center"
          height="250px"
          mt="-20px"
        >
          <FleetFuelLevelChart
            ApexGaugeChart={ApexGaugeChart}
            selectedData={selectedData}
          />
        </Box>
      </Box>
      <Box
        gridColumn="span 4"
        gridRow="span 2"
        backgroundColor={colors.primary[400]}
        padding="30px"
      >
        <Box
          display="flex"
          alignItems="center"
          justifyContent="center"
          height="200px"
        ></Box>
      </Box>

      <OverviewDataBox colors={colors} stats={stats} />
    </>
  );
};

export default StatisticsPanel;

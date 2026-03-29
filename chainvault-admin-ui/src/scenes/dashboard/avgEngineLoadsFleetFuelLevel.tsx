import React, { useState } from "react";
import { Box, Typography } from "@mui/material";
import Plot from "react-plotly.js";
import Chart from "react-apexcharts";
import { fleetData } from "../../data/fleetData";
import AvgEngineLoadsChart from "./avgEngineLoadsChart";
import FleetFuelLevelChart from "./fleetFuelLevelChart";
import FeetOverviewDataBox from "./feetOverviewDataBox";
import Geography from "../geography";
import { GeographyChart } from "../../components";
import { availableMetrics } from "../../data/availableMetrics";
import MetricTrendModal from "./metricTrendModal";

const AvgEngineLoadsFleetFuelLevel = ({
  theme,
  colors,
  isXlDevices,
  selectedData,
}) => {
  // const [selectedRegion] = useState(Object.keys(fleetData)[0]);
  const [modalMetricTitle, setModalMetricTitle] = useState();
  const [modalChartType, setModalChartType] = useState("line");
  const [modalMetricData, setModalMetricData] = useState([]);
  const [isMetricTrendModalOpen, setIsMetricTrendModalOpen] = useState(false);
  // const selectedData = fleetData[selectedRegion];

  const openMetricTrendModal = (metricType, metricValue, chartType) => {
    setModalMetricTitle(`Historical Trend (${metricType})`);
    setModalChartType(chartType);

    const metricConfig = availableMetrics.find((m) => m?.label === metricType);
    const dataPoints = [];
    const labels = [];

    for (let i = 0; i < 24; i++) {
      labels.push(`${i}:00`);
      const fluctuation =
        (Math.random() - 0.5) * (metricConfig?.fluctuation || 20);
      dataPoints.push(
        Math.max(
          0,
          Math.min(metricConfig?.max || 100, metricValue + fluctuation)
        )
      );
    }
    setModalMetricData(
      dataPoints.map((value, index) => ({ name: labels[index], value: value }))
    );
    setIsMetricTrendModalOpen(true);
  };

  const ApexGaugeChart = ({ title, value }) => {
    const getApexColor = (val) => {
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

  return (
    <>
      <Box
        gridColumn={isXlDevices ? "span 4" : "span 4"}
        gridRow="span 2"
        backgroundColor={colors.primary[400]}
        p="30px"
        sx={{ cursor: "pointer" }}
        onClick={() =>
          openMetricTrendModal(
            "Average Engine Load",
            selectedData?.avgEngineLoad,
            "line"
          )
        }
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
        gridColumn={isXlDevices ? "span 4" : "span 4"}
        gridRow="span 2"
        backgroundColor={colors.primary[400]}
        p="30px"
        sx={{ cursor: "pointer" }}
        onClick={() =>
          openMetricTrendModal(
            "Fleet Fuel Level",
            selectedData?.fleetFuelLevel,
            "line"
          )
        }
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
            PlotlyGaugeChart={PlotlyGaugeChart}
            selectedData={selectedData}
          />
        </Box>
      </Box>

      <Box
        gridColumn={isXlDevices ? "span 4" : "span 4"}
        gridRow="span 2"
        backgroundColor={colors.primary[400]}
        padding="30px"
      >
        <Typography variant="h5" fontWeight="600" mb="15px">
          Geography Based Traffic
        </Typography>
        <Box
          display="flex"
          alignItems="center"
          justifyContent="center"
          height="200px"
        >
          <GeographyChart isDashboard={true} />
        </Box>
      </Box>

      <FeetOverviewDataBox
        colors={colors}
        selectedData={selectedData}
        openMetricTrendModal={openMetricTrendModal}
      />
      <MetricTrendModal
        modalMetricTitle={modalMetricTitle}
        modalChartType={modalChartType}
        modalMetricData={modalMetricData}
        isOpen={isMetricTrendModalOpen}
        onClose={() => setIsMetricTrendModalOpen(false)}
        colors={colors}
      />
    </>
  );
};

export default AvgEngineLoadsFleetFuelLevel;

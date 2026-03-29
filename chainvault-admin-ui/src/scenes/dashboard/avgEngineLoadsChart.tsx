import React from "react";

const AvgEngineLoadsChart = ({ ApexGaugeChart, PlotlyGaugeChart, selectedData }) => {

  return (
    <>
      <ApexGaugeChart
        title="Avg Engine Load"
        value={selectedData?.avgEngineLoad}
      />
      {/* <PlotlyGaugeChart
        title="Avg Engine Load"
        value={selectedData?.avgEngineLoad}
      /> */}
    </>
  );
};

export default AvgEngineLoadsChart;

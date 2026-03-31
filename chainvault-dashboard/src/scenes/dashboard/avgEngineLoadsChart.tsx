/*
 * Copyright (c) 2026. Gryphus Lab
 */
const AvgEngineLoadsChart = ({ ApexGaugeChart, selectedData }) => {
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

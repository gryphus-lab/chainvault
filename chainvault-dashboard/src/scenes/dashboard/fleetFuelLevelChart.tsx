/*
 * Copyright (c) 2026. Gryphus Lab
 */
const FleetFuelLevelChart = ({ ApexGaugeChart, selectedData }) => {
  return (
    <>
      <ApexGaugeChart
        title="Fleet Fuel Level"
        value={selectedData?.fleetFuelLevel}
      />
      {/* <PlotlyGaugeChart
        title="Fleet Fuel Level"
        value={selectedData?.fleetFuelLevel}
      /> */}
    </>
  );
};

export default FleetFuelLevelChart;

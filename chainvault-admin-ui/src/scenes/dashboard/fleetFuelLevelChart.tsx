import React from "react";

const FleetFuelLevelChart = ({ ApexGaugeChart, PlotlyGaugeChart, selectedData }) => {

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

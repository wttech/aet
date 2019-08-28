import React from 'react';
import FeaturesList from "../containers/sidebars/FeaturesList";

export const CreateFeatureList = () => {
  return (
    <div className="features-container">
    <FeaturesList type="collectors"/>
    <FeaturesList type="comparators"/>
    <FeaturesList type="modifiers"/>
    <FeaturesList type="data-filters"/>
  </div>
  )
}
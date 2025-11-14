import React from 'react';

const ListScoredOrganismsScreen: React.FC = () => {
  return (
    <div className="repository-view">
      <h1>List Scored Organisms</h1>
      <div className="status-section">
        <h2>All Scored Organisms</h2>
        <p>List of all scored organisms will be displayed here.</p>
        {/* TODO: Implement API call to fetch and display organisms */}
      </div>
    </div>
  );
};

export default ListScoredOrganismsScreen;
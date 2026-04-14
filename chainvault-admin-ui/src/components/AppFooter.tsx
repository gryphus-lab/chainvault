/*
 * Copyright (c) 2026. Gryphus Lab
 */
import React from 'react'
import { CFooter } from '@coreui/react'

const AppFooter = () => {
  return (
    <CFooter className="px-4">
      <div>
        <a
          href="https://github.com/gryphus-lab/chainvault"
          target="_blank"
          rel="noopener noreferrer"
        >
          Chainvault - Migration Dashboard
        </a>
        <span className="ms-1">&copy; 2026 Gryphus Lab.</span>
      </div>
      <div className="ms-auto">
        <span className="me-1">Powered by</span>
        <a
          href="https://coreui.io/product/free-react-admin-template/"
          target="_blank"
          rel="noopener noreferrer"
        >
          CoreUI React Admin &amp; Dashboard Template
        </a>
      </div>
    </CFooter>
  )
}

export default React.memo(AppFooter)

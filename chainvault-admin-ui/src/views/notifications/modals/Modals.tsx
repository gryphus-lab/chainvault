/*
 * Copyright (c) 2026. Gryphus Lab
 */
import React, { useState } from 'react'
import {
  CButton,
  CCard,
  CCardBody,
  CCardHeader,
  CCarousel,
  CCarouselItem,
  CCarouselCaption,
  CCol,
  CRow,
  CNav,
  CNavItem,
  CNavLink,
  CModal,
  CModalHeader,
  CModalTitle,
  CModalBody,
  CModalFooter,
  CWidgetStatsD,
} from '@coreui/react'
import { CChart } from '@coreui/react-chartjs'
import CIcon from '@coreui/icons-react'
import { cibFacebook, cibTwitter, cibLinkedin } from '@coreui/icons'

// =========================
// 🔁 REUSABLE COMPONENTS
// =========================

// Modal
const AppModal = ({ title, trigger, children, ...props }: any) => {
  const [visible, setVisible] = useState(false)

  return (
    <>
      <CButton onClick={() => setVisible(true)}>{trigger}</CButton>

      <CModal visible={visible} onClose={() => setVisible(false)} {...props}>
        <CModalHeader>
          <CModalTitle>{title}</CModalTitle>
        </CModalHeader>

        <CModalBody>{children}</CModalBody>

        <CModalFooter>
          <CButton color="secondary" onClick={() => setVisible(false)}>
            Close
          </CButton>
          <CButton color="primary">Save</CButton>
        </CModalFooter>
      </CModal>
    </>
  )
}

// Chart
const ChartWidget = ({ data }: { data: number[] }) => {
  const options = {
    elements: { line: { tension: 0.4 }, point: { radius: 0 } },
    maintainAspectRatio: false,
    plugins: { legend: { display: false } },
    scales: { x: { display: false }, y: { display: false } },
  }

  return (
    <CChart
      className="position-absolute w-100 h-100"
      type="line"
      data={{
        labels: ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul'],
        datasets: [
          {
            backgroundColor: 'rgba(255,255,255,.1)',
            borderColor: 'rgba(255,255,255,.55)',
            borderWidth: 2,
            data,
            fill: true,
          },
        ],
      }}
      options={options}
    />
  )
}

// Widget
const BrandWidget = ({ icon, values, color, chartData }: any) => (
  <CWidgetStatsD
    icon={icon}
    values={values}
    {...(chartData && { chart: <ChartWidget data={chartData} /> })}
    style={color ? { '--cui-card-cap-bg': color } : undefined}
  />
)

// Carousel
const AppCarousel = ({ items, withCaption, ...props }: any) => (
  <CCarousel {...props}>
    {items.map((item: any, i: number) => (
      <CCarouselItem key={item.label}>
        <img className="d-block w-100" src={item.src} alt={`slide ${i}`} />
        {withCaption && (
          <CCarouselCaption>
            <h5>{item.title}</h5>
            <p>{item.text}</p>
          </CCarouselCaption>
        )}
      </CCarouselItem>
    ))}
  </CCarousel>
)

// Nav
const AppNav = ({ items, ...props }: any) => (
  <CNav {...props}>
    {items.map((item: any) => (
      <CNavItem key={item.label}>
        <CNavLink active={item.active} disabled={item.disabled}>
          {item.label}
        </CNavLink>
      </CNavItem>
    ))}
  </CNav>
)

// =========================
// 🚀 MAIN COMPONENT
// =========================

const DashboardDemo = () => {
  const navItems = [
    { label: 'Active', active: true },
    { label: 'Link' },
    { label: 'Disabled', disabled: true },
  ]

  const carouselItems = [
    { src: '/img1.jpg', title: 'First', text: 'First slide' },
    { src: '/img2.jpg', title: 'Second', text: 'Second slide' },
    { src: '/img3.jpg', title: 'Third', text: 'Third slide' },
  ]

  return (
    <CRow>
      {/* NAV */}
      <CCol xs={12}>
        <CCard className="mb-4">
          <CCardHeader>Nav</CCardHeader>
          <CCardBody>
            <AppNav items={navItems} />
            <AppNav items={navItems} className="justify-content-center mt-3" />
            <AppNav items={navItems} variant="tabs" className="mt-3" />
          </CCardBody>
        </CCard>
      </CCol>

      {/* CAROUSEL */}
      <CCol xs={12}>
        <CCard className="mb-4">
          <CCardHeader>Carousel</CCardHeader>
          <CCardBody>
            <AppCarousel items={carouselItems} controls indicators />
            <AppCarousel items={carouselItems} withCaption className="mt-4" />
          </CCardBody>
        </CCard>
      </CCol>

      {/* WIDGETS */}
      <CCol xs={12}>
        <CCard className="mb-4">
          <CCardHeader>Widgets</CCardHeader>
          <CCardBody>
            <CRow>
              <CCol md={4}>
                <BrandWidget
                  icon={<CIcon icon={cibFacebook} height={40} />}
                  values={[{ title: 'friends', value: '89K' }]}
                  color="#3b5998"
                  chartData={[65, 59, 84, 84, 51, 55, 40]}
                />
              </CCol>

              <CCol md={4}>
                <BrandWidget
                  icon={<CIcon icon={cibTwitter} height={40} />}
                  values={[{ title: 'followers', value: '973K' }]}
                  color="#00aced"
                  chartData={[1, 13, 9, 17, 34, 41, 38]}
                />
              </CCol>

              <CCol md={4}>
                <BrandWidget
                  icon={<CIcon icon={cibLinkedin} height={40} />}
                  values={[{ title: 'contacts', value: '500' }]}
                  color="#4875b4"
                  chartData={[78, 81, 80, 45, 34, 12, 40]}
                />
              </CCol>
            </CRow>
          </CCardBody>
        </CCard>
      </CCol>

      {/* MODALS */}
      <CCol xs={12}>
        <CCard className="mb-4">
          <CCardHeader>Modals</CCardHeader>
          <CCardBody>
            <AppModal title="Basic Modal" trigger="Open Modal">
              Simple modal content
            </AppModal>

            <AppModal title="Scrollable Modal" trigger="Scrollable" scrollable>
              Long content here...
            </AppModal>

            <AppModal title="Centered Modal" trigger="Centered" alignment="center">
              Centered modal
            </AppModal>

            <AppModal title="Fullscreen Modal" trigger="Fullscreen" fullscreen>
              Fullscreen modal
            </AppModal>
          </CCardBody>
        </CCard>
      </CCol>
    </CRow>
  )
}

export default DashboardDemo

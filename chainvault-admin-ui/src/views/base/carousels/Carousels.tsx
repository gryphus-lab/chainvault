/*
 * Copyright (c) 2026. Gryphus Lab
 */
import React from 'react'
import {
  CCard,
  CCardBody,
  CCardHeader,
  CCarousel,
  CCarouselCaption,
  CCarouselItem,
  CCol,
  CRow,
} from '@coreui/react'
import { DocsComponents, DocsExample } from '../../../components'

import AngularImg from '../../../assets/images/angular.jpg'
import ReactImg from '../../../assets/images/react.jpg'
import VueImg from '../../../assets/images/vue.jpg'

/**
 * Placeholder slides used for light/dark carousel examples
 *
 * @type {string[]}
 */
const slidesLight = [
  'data:image/svg+xml;charset=UTF-8,...',
  'data:image/svg+xml;charset=UTF-8,...',
  'data:image/svg+xml;charset=UTF-8,...',
]

const Carousels = () => {
  return (
    <CRow>
      {/* Documentation header */}
      <CCol xs={12}>
        <DocsComponents href="components/carousel/" />

        {/* Slide-only carousel */}
        <CCard className="mb-4">
          <CCardHeader>
            <strong>Carousel</strong> <small>Slide only</small>
          </CCardHeader>
          <CCardBody>
            <p className="text-body-secondary small">
              Basic carousel displaying slides without controls or indicators.
            </p>
            <DocsExample href="components/carousel">
              <CCarousel>
                <CCarouselItem>
                  <img className="d-block w-100" src={ReactImg} alt="React slide" />
                </CCarouselItem>
                <CCarouselItem>
                  <img className="d-block w-100" src={AngularImg} alt="Angular slide" />
                </CCarouselItem>
                <CCarouselItem>
                  <img className="d-block w-100" src={VueImg} alt="Vue slide" />
                </CCarouselItem>
              </CCarousel>
            </DocsExample>
          </CCardBody>
        </CCard>
      </CCol>

      {/* Controls */}
      <CCol xs={12}>
        <CCard className="mb-4">
          <CCardHeader>
            <strong>Carousel</strong> <small>With controls</small>
          </CCardHeader>
          <CCardBody>
            <p className="text-body-secondary small">Adds previous/next navigation controls.</p>
            <DocsExample href="components/carousel/#with-controls">
              <CCarousel controls>
                <CCarouselItem>
                  <img className="d-block w-100" src={ReactImg} alt="React slide" />
                </CCarouselItem>
                <CCarouselItem>
                  <img className="d-block w-100" src={AngularImg} alt="Angular slide" />
                </CCarouselItem>
                <CCarouselItem>
                  <img className="d-block w-100" src={VueImg} alt="Vue slide" />
                </CCarouselItem>
              </CCarousel>
            </DocsExample>
          </CCardBody>
        </CCard>
      </CCol>

      {/* Indicators */}
      <CCol xs={12}>
        <CCard className="mb-4">
          <CCardHeader>
            <strong>Carousel</strong> <small>With indicators</small>
          </CCardHeader>
          <CCardBody>
            <p className="text-body-secondary small">Adds slide indicators alongside controls.</p>
            <DocsExample href="components/carousel/#with-indicators">
              <CCarousel controls indicators>
                <CCarouselItem>
                  <img className="d-block w-100" src={ReactImg} alt="React slide" />
                </CCarouselItem>
                <CCarouselItem>
                  <img className="d-block w-100" src={AngularImg} alt="Angular slide" />
                </CCarouselItem>
                <CCarouselItem>
                  <img className="d-block w-100" src={VueImg} alt="Vue slide" />
                </CCarouselItem>
              </CCarousel>
            </DocsExample>
          </CCardBody>
        </CCard>
      </CCol>

      {/* Captions */}
      <CCol xs={12}>
        <CCard className="mb-4">
          <CCardHeader>
            <strong>Carousel</strong> <small>With captions</small>
          </CCardHeader>
          <CCardBody>
            <p className="text-body-secondary small">
              Demonstrates captions with responsive visibility.
            </p>
            <DocsExample href="components/carousel/#with-captions">
              <CCarousel controls indicators>
                <CCarouselItem>
                  <img className="d-block w-100" src={ReactImg} alt="React slide" />
                  <CCarouselCaption className="d-none d-md-block">
                    <h5>First slide</h5>
                    <p>Example caption text.</p>
                  </CCarouselCaption>
                </CCarouselItem>
              </CCarousel>
            </DocsExample>
          </CCardBody>
        </CCard>
      </CCol>

      {/* Crossfade */}
      <CCol xs={12}>
        <CCard className="mb-4">
          <CCardHeader>
            <strong>Carousel</strong> <small>Crossfade</small>
          </CCardHeader>
          <CCardBody>
            <p className="text-body-secondary small">Uses fade transition instead of sliding.</p>
            <DocsExample href="components/carousel/#crossfade">
              <CCarousel controls transition="crossfade">
                <CCarouselItem>
                  <img className="d-block w-100" src={ReactImg} alt="React slide" />
                </CCarouselItem>
              </CCarousel>
            </DocsExample>
          </CCardBody>
        </CCard>
      </CCol>

      {/* Dark variant */}
      <CCol xs={12}>
        <CCard className="mb-4">
          <CCardHeader>
            <strong>Carousel</strong> <small>Dark variant</small>
          </CCardHeader>
          <CCardBody>
            <p className="text-body-secondary small">
              Demonstrates dark-themed carousel controls and captions.
            </p>
            <DocsExample href="components/carousel/#dark-variant">
              <CCarousel controls indicators dark>
                <CCarouselItem>
                  <img className="d-block w-100" src={slidesLight[0]} alt="Slide" />
                </CCarouselItem>
              </CCarousel>
            </DocsExample>
          </CCardBody>
        </CCard>
      </CCol>
    </CRow>
  )
}

export default Carousels

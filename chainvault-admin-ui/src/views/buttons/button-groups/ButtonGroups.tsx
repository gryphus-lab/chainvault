/*
 * Copyright (c) 2026. Gryphus Lab
 */
import React, { ReactNode } from 'react'
import {
  CButton,
  CButtonGroup,
  CButtonToolbar,
  CCard,
  CCardBody,
  CCardHeader,
  CCol,
  CDropdown,
  CDropdownDivider,
  CDropdownItem,
  CDropdownMenu,
  CDropdownToggle,
  CFormCheck,
  CRow,
} from '@coreui/react'
import { DocsComponents, DocsExample } from '../../../components'

/* ------------------ Types ------------------ */

type SectionProps = {
  title: string
  subtitle?: string
  children: ReactNode
  href: string
}

// Fixed TS2322: Specified literal types for color to match CoreUI expectations
type ButtonConfig = {
  label: ReactNode
  color?:
    | 'primary'
    | 'secondary'
    | 'success'
    | 'danger'
    | 'warning'
    | 'info'
    | 'light'
    | 'dark'
    | 'link'
  href?: string
  active?: boolean
}

type ButtonGroupExampleProps = {
  buttons: ButtonConfig[]
  variant?: 'outline' | 'ghost'
  size?: 'sm' | 'lg'
  vertical?: boolean
}

/* ------------------ Reusable Components ------------------ */

const Section = ({ title, subtitle, children, href }: SectionProps) => (
  <CCol xs={12}>
    <CCard className="mb-4">
      <CCardHeader>
        <strong>{title}</strong> {subtitle && <span>{subtitle}</span>}
      </CCardHeader>
      <CCardBody>
        <DocsExample href={href}>{children}</DocsExample>
      </CCardBody>
    </CCard>
  </CCol>
)

const ButtonGroupExample = ({
  buttons,
  variant,
  size,
  vertical = false,
}: ButtonGroupExampleProps) => (
  <CButtonGroup size={size} vertical={vertical}>
    {buttons.map((btn, idx) => {
      // Fixed TS2710: Extract 'label' so it isn't spread into CButton
      const { label, ...rest } = btn
      return (
        <CButton key={btn.href ?? idx} {...rest} variant={variant}>
          {label}
        </CButton>
      )
    })}
  </CButtonGroup>
)

type CheckboxGroupProps = {
  type?: 'checkbox' | 'radio'
  name?: string
}

const CheckboxGroup = ({ type = 'checkbox', name }: CheckboxGroupProps) => (
  <CButtonGroup>
    {[1, 2, 3].map((i) => (
      <CFormCheck
        key={i}
        type={type}
        name={name}
        id={`${name ?? type}${i}`}
        autoComplete="off"
        label={`${type === 'radio' ? 'Radio' : 'Checkbox'} ${i}`}
        // Fixed variant literal type
        button={{ variant: 'outline' as const }}
      />
    ))}
  </CButtonGroup>
)

type DropdownGroupProps = {
  color?: ButtonConfig['color']
}

const DropdownGroup = ({ color = 'primary' }: DropdownGroupProps) => (
  <CDropdown variant="btn-group">
    <CDropdownToggle color={color}>Dropdown</CDropdownToggle>
    <CDropdownMenu>
      {['Action', 'Another action', 'Something else here'].map((t) => (
        <CDropdownItem key={t}>{t}</CDropdownItem>
      ))}
      <CDropdownDivider />
      <CDropdownItem>Separated link</CDropdownItem>
    </CDropdownMenu>
  </CDropdown>
)

/* ------------------ Main Component ------------------ */

const ButtonGroups = () => {
  return (
    <CRow>
      <CCol xs={12}>
        <DocsComponents href="components/button-group/" />
      </CCol>

      <Section title="React Button Group" subtitle="Basic" href="components/button-group/">
        <ButtonGroupExample
          buttons={[
            { color: 'primary', label: 'Left' },
            { color: 'primary', label: 'Middle' },
            { color: 'primary', label: 'Right' },
          ]}
        />
      </Section>

      <Section
        title="Mixed Styles Group"
        subtitle="Mixed"
        href="components/button-group/#mixed-styles"
      >
        <ButtonGroupExample
          buttons={[
            { color: 'danger', label: 'Left' },
            { color: 'warning', label: 'Middle' },
            { color: 'success', label: 'Right' },
          ]}
        />
      </Section>

      <Section
        title="Outlined Group"
        subtitle="Outlined"
        href="components/button-group/#outlined-styles"
      >
        <ButtonGroupExample
          variant="outline"
          buttons={[
            { color: 'primary', label: 'Left' },
            { color: 'primary', label: 'Middle' },
            { color: 'primary', label: 'Right' },
          ]}
        />
      </Section>

      <Section
        title="Checkbox & Radio"
        href="components/button-group/#checkbox-and-radio-button-groups"
      >
        <CheckboxGroup />
        <br />
        <CheckboxGroup type="radio" name="radioGroup" />
      </Section>

      <Section title="Toolbar" href="components/button-group/#button-toolbar">
        <CButtonToolbar>
          {(
            [
              ['primary', [1, 2, 3, 4]],
              ['secondary', [5, 6, 7]],
            ] as const
          ).map(([color, nums], idx) => (
            <CButtonGroup key={`${color}-${nums.concat()}-${idx}}`} className="me-2">
              {nums.map((n) => (
                <CButton key={n} color={color}>
                  {n}
                </CButton>
              ))}
            </CButtonGroup>
          ))}
        </CButtonToolbar>
      </Section>

      <Section title="Sizing" href="components/button-group/#sizing">
        {(['lg', undefined, 'sm'] as const).map((size, idx) => (
          <div key={`${size}-${idx}`}>
            <ButtonGroupExample
              size={size}
              variant="outline"
              buttons={[
                { color: 'dark', label: 'Left' },
                { color: 'dark', label: 'Middle' },
                { color: 'dark', label: 'Right' },
              ]}
            />
            <br />
          </div>
        ))}
      </Section>

      <Section title="Nesting" href="components/button-group/#nesting">
        <CButtonGroup>
          <CButton color="primary">1</CButton>
          <CButton color="primary">2</CButton>
          <DropdownGroup />
        </CButtonGroup>
      </Section>

      <Section title="Vertical" href="components/button-group/#vertical-variation">
        <ButtonGroupExample
          vertical
          buttons={Array.from({ length: 5 }, (_, i) => ({
            color: 'dark',
            label: `Button ${i + 1}`,
          }))}
        />
      </Section>
    </CRow>
  )
}

export default ButtonGroups

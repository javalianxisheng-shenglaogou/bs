/**
 * English Language Pack - Site Module
 */
export default {
  title: 'Site Management',
  list: 'Site List',
  add: 'Create Site',
  edit: 'Edit Site',
  delete: 'Delete Site',
  detail: 'Site Details',
  
  fields: {
    id: 'ID',
    name: 'Site Name',
    code: 'Site Code',
    domain: 'Domain',
    description: 'Description',
    status: 'Status',
    language: 'Language',
    timezone: 'Timezone',
    theme: 'Theme',
    logo: 'Logo',
    favicon: 'Favicon',
    seoTitle: 'SEO Title',
    seoKeywords: 'SEO Keywords',
    seoDescription: 'SEO Description',
    contactEmail: 'Contact Email',
    contactPhone: 'Contact Phone',
    contactAddress: 'Contact Address',
    isDefault: 'Default Site',
    createdAt: 'Created At',
    updatedAt: 'Updated At'
  },
  
  status: {
    active: 'Active',
    inactive: 'Inactive',
    maintenance: 'Maintenance',
    ACTIVE: 'Running',
    INACTIVE: 'Disabled'
  },
  
  placeholder: {
    name: 'Enter site name',
    code: 'Enter site code (lowercase letters, numbers, underscores, hyphens)',
    domain: 'Enter site domain',
    description: 'Enter site description',
    searchKeyword: 'Search site name or domain',
    selectStatus: 'Select status',
    selectLanguage: 'Select default language',
    selectTimezone: 'Select timezone',
    selectSite: 'Select Site',
    seoTitle: 'Enter SEO title',
    seoKeywords: 'Enter SEO keywords, separated by commas',
    seoDescription: 'Enter SEO description',
    contactEmail: 'Enter contact email',
    contactPhone: 'Enter contact phone',
    contactAddress: 'Enter contact address',
    customCss: 'Enter custom CSS code',
    customJs: 'Enter custom JavaScript code'
  },
  
  validation: {
    nameRequired: 'Site name is required',
    nameLength: 'Site name must be 1-100 characters',
    nameMaxLength: 'Site name cannot exceed 100 characters',
    codeRequired: 'Site code is required',
    codeMaxLength: 'Site code cannot exceed 50 characters',
    codePattern: 'Site code can only contain lowercase letters, numbers, underscores and hyphens',
    domainRequired: 'Site domain is required',
    domainMaxLength: 'Site domain cannot exceed 255 characters',
    domainInvalid: 'Invalid domain format',
    siteRequired: 'Please select a site'
  },
  
  message: {
    addSuccess: 'Site created successfully',
    addFailed: 'Failed to create site',
    updateSuccess: 'Site updated successfully',
    updateFailed: 'Failed to update site',
    deleteSuccess: 'Site deleted successfully',
    deleteFailed: 'Failed to delete site',
    deleteConfirm: 'Are you sure you want to delete site "{name}"?',
    loadFailed: 'Failed to load site list',
    createSuccess: 'Created successfully',
    saveFailed: 'Failed to save site',
    configSaveSuccess: 'Configuration saved successfully',
    configSaveFailed: 'Failed to save configuration',
    logoUploadSuccess: 'Logo uploaded successfully',
    faviconUploadSuccess: 'Favicon uploaded successfully'
  },

  // Additional content
  addSite: 'Add Site',
  config: 'Config',
  empty: 'No site data',
  noDescription: 'No description',
  defaultSite: 'Default Site',

  // Configuration related
  siteConfig: 'Site Configuration',
  basicConfig: 'Basic Configuration',
  themeConfig: 'Theme Configuration',
  advancedConfig: 'Advanced Configuration',
  primaryColor: 'Primary Color',
  secondaryColor: 'Secondary Color',
  customCss: 'Custom CSS',
  customJs: 'Custom JS',
  saveConfig: 'Save Configuration',

  // SEO settings
  seoSettings: 'SEO Settings',
  contactInfo: 'Contact Information',

  // Language options
  languages: {
    zh_CN: 'Simplified Chinese',
    en_US: 'English',
    zh_TW: 'Traditional Chinese'
  }
}


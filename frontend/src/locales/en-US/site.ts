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
    createdAt: 'Created At',
    updatedAt: 'Updated At'
  },
  
  status: {
    active: 'Active',
    inactive: 'Inactive',
    maintenance: 'Maintenance'
  },
  
  placeholder: {
    name: 'Enter site name',
    domain: 'Enter domain',
    description: 'Enter description',
    searchKeyword: 'Search site name or domain',
    selectStatus: 'Select status',
    selectLanguage: 'Select language',
    selectTimezone: 'Select timezone'
  },
  
  validation: {
    nameRequired: 'Site name is required',
    nameLength: 'Site name must be 1-100 characters',
    domainRequired: 'Domain is required',
    domainInvalid: 'Invalid domain format'
  },
  
  message: {
    addSuccess: 'Site created successfully',
    addFailed: 'Failed to create site',
    updateSuccess: 'Site updated successfully',
    updateFailed: 'Failed to update site',
    deleteSuccess: 'Site deleted successfully',
    deleteFailed: 'Failed to delete site',
    deleteConfirm: 'Are you sure you want to delete site "{name}"?',
    loadFailed: 'Failed to load site list'
  }
}


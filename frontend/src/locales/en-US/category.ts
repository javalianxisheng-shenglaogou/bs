/**
 * English Language Pack - Category Module
 */
export default {
  title: 'Category Management',
  list: 'Category List',
  add: 'Add Category',
  addChild: 'Add Subcategory',
  edit: 'Edit Category',
  delete: 'Delete Category',
  query: 'Query',
  
  fields: {
    id: 'ID',
    name: 'Category Name',
    code: 'Category Code',
    description: 'Description',
    sortOrder: 'Sort Order',
    level: 'Level',
    isVisible: 'Visibility',
    iconUrl: 'Category Icon',
    coverUrl: 'Cover Image',
    seoTitle: 'SEO Title',
    seoKeywords: 'SEO Keywords',
    seoDescription: 'SEO Description',
    parentId: 'Parent Category',
    createdAt: 'Created At'
  },
  
  status: {
    visible: 'Visible',
    hidden: 'Hidden'
  },
  
  actions: {
    addChild: 'Subcategory',
    edit: 'Edit',
    show: 'Show',
    hide: 'Hide',
    delete: 'Delete'
  },
  
  tabs: {
    basic: 'Basic Information',
    images: 'Image Settings',
    seo: 'SEO Settings'
  },
  
  placeholder: {
    selectSite: 'Please select a site',
    selectParent: 'Please select parent category (leave empty for top level)',
    name: 'Enter category name',
    code: 'Enter category code (letters, numbers, underscores)',
    description: 'Enter category description',
    seoTitle: 'Enter SEO title (leave empty to use category name)',
    seoKeywords: 'Enter SEO keywords, separated by commas',
    seoDescription: 'Enter SEO description'
  },
  
  validation: {
    siteRequired: 'Please select a site',
    nameRequired: 'Category name is required',
    nameMaxLength: 'Category name cannot exceed 100 characters',
    codeRequired: 'Category code is required',
    codeMaxLength: 'Category code cannot exceed 50 characters',
    codePattern: 'Category code can only contain letters, numbers and underscores'
  },
  
  message: {
    loadSitesFailed: 'Failed to load site list',
    loadTreeFailed: 'Failed to load category tree',
    selectSiteFirst: 'Please select a site first',
    createSuccess: 'Created successfully',
    updateSuccess: 'Updated successfully',
    deleteSuccess: 'Deleted successfully',
    saveFailed: 'Failed to save category',
    deleteFailed: 'Failed to delete category',
    deleteConfirm: 'Are you sure you want to delete category "{name}"?',
    uploadSuccess: 'Uploaded successfully',
    uploadFailed: 'Upload failed',
    iconUploadSuccess: 'Icon uploaded successfully',
    coverUploadSuccess: 'Cover uploaded successfully',
    onlyJpgPng: 'Only JPG/PNG format images are allowed!',
    sizeTooLarge: 'Image size cannot exceed 2MB!'
  },
  
  tips: {
    sortOrder: 'Smaller numbers appear first',
    iconSize: 'Recommended size: 64x64px, supports jpg/png format, max 2MB',
    coverSize: 'Recommended size: 800x450px, supports jpg/png format, max 2MB',
    deleteIcon: 'Delete Icon',
    deleteCover: 'Delete Cover'
  }
}

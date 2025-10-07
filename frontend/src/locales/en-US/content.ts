/**
 * English Language Pack - Content Module
 */
export default {
  title: 'Content Management',
  list: 'Content List',
  add: 'Create Content',
  edit: 'Edit Content',
  delete: 'Delete Content',
  detail: 'Content Details',
  publish: 'Publish Content',
  unpublish: 'Unpublish Content',
  
  fields: {
    id: 'ID',
    title: 'Title',
    slug: 'Slug',
    content: 'Content',
    excerpt: 'Excerpt',
    author: 'Author',
    category: 'Category',
    tags: 'Tags',
    status: 'Status',
    publishedAt: 'Published At',
    createdAt: 'Created At',
    updatedAt: 'Updated At',
    views: 'Views',
    featured: 'Featured',
    seoTitle: 'SEO Title',
    seoKeywords: 'SEO Keywords',
    seoDescription: 'SEO Description',
    contentType: 'Content Type',
    approvalStatus: 'Approval Status',
    authorName: 'Author',
    viewCount: 'Views',
    isTop: 'Top',
    isFeatured: 'Featured',
    coverImage: 'Cover Image',
    attributes: 'Content Attributes',
    isOriginal: 'Original'
  },
  
  status: {
    draft: 'Draft',
    pending: 'Pending',
    approved: 'Approved',
    rejected: 'Rejected',
    published: 'Published',
    archived: 'Archived'
  },

  contentTypes: {
    article: 'Article',
    page: 'Page',
    news: 'News',
    product: 'Product',
    custom: 'Custom',
    other: 'Other'
  },

  approvalStatus: {
    none: 'Not Submitted',
    pending: 'Pending',
    approved: 'Approved',
    rejected: 'Rejected'
  },
  
  placeholder: {
    title: 'Enter title',
    slug: 'Enter slug',
    content: 'Enter content',
    excerpt: 'Enter excerpt',
    searchKeyword: 'Search title or content',
    selectCategory: 'Select category',
    selectStatus: 'Select status',
    selectTags: 'Select tags',
    selectContentType: 'Select content type'
  },
  
  validation: {
    titleRequired: 'Title is required',
    titleLength: 'Title must be 1-200 characters',
    contentRequired: 'Content is required',
    categoryRequired: 'Category is required',
    slugRequired: 'URL slug is required',
    slugLength: 'URL slug length cannot exceed 200 characters',
    slugPattern: 'URL slug can only contain lowercase letters, numbers and hyphens'
  },
  
  message: {
    addSuccess: 'Content created successfully',
    addFailed: 'Failed to create content',
    updateSuccess: 'Content updated successfully',
    updateFailed: 'Failed to update content',
    deleteSuccess: 'Content deleted successfully',
    deleteFailed: 'Failed to delete content',
    publishSuccess: 'Content published successfully',
    publishFailed: 'Failed to publish content',
    unpublishSuccess: 'Content unpublished successfully',
    unpublishFailed: 'Failed to unpublish content',
    deleteConfirm: 'Are you sure you want to delete content "{title}"?',
    loadFailed: 'Failed to load content list',
    cannotEditPublished: 'Published content cannot be edited directly, please unpublish first',
    preview: 'Preview',
    uploadSuccess: 'Cover image uploaded successfully',
    uploadFailed: 'Failed to upload cover image',
    uploadFormatError: 'Cover image must be JPG/PNG format!',
    uploadSizeError: 'Cover image size cannot exceed 2MB!'
  },
  
  category: {
    title: 'Category Management',
    add: 'Add Category',
    edit: 'Edit Category',
    delete: 'Delete Category',
    name: 'Category Name',
    slug: 'Category Slug',
    description: 'Description',
    parent: 'Parent Category'
  },

  // Version control related translations
  version: {
    title: 'Version Control',
    history: 'Version History',
    detail: 'Version Details',
    compare: 'Version Compare',
    restore: 'Version Restore',
    tag: 'Version Tag',
    delete: 'Delete Version',

    fields: {
      versionNumber: 'Version',
      changeType: 'Change Type',
      changeSummary: 'Change Summary',
      createdBy: 'Changed By',
      createdAt: 'Changed At',
      tagName: 'Tag',
      currentVersion: 'Current Version',
      totalVersions: 'Total Versions'
    },

    changeTypes: {
      CREATE: 'Create',
      UPDATE: 'Update',
      PUBLISH: 'Publish',
      UNPUBLISH: 'Unpublish',
      RESTORE: 'Restore',
      STATUS_CHANGE: 'Status Change'
    },

    actions: {
      view: 'View',
      restore: 'Restore',
      tag: 'Tag',
      delete: 'Delete',
      compare: 'Compare',
      compareSelected: 'Compare Selected Versions',
      viewHistory: 'View and manage content version history'
    },

    messages: {
      restoreSuccess: 'Version restored successfully',
      restoreFailed: 'Failed to restore version',
      tagSuccess: 'Version tagged successfully',
      tagFailed: 'Failed to tag version',
      deleteSuccess: 'Version deleted successfully',
      deleteFailed: 'Failed to delete version',
      loadFailed: 'Failed to load version list',
      restoreConfirm: 'Are you sure you want to restore to version {version}?',
      deleteConfirm: 'Are you sure you want to delete version {version}? This action cannot be undone!',
      selectTwoVersions: 'Please select two versions to compare',
      noVersions: 'No version records'
    },

    dialog: {
      versionDetail: 'Version Details',
      versionCompare: 'Version Compare',
      versionA: 'Version A ({version})',
      versionB: 'Version B ({version})',
      tagVersion: 'Tag Version',
      tagName: 'Tag Name',
      tagPlaceholder: 'Enter tag name, e.g., milestone, pre-release, etc.'
    },

    tagged: 'Tagged',
    diffFields: 'Difference Fields',
    changed: 'Changed',
    unchanged: 'Unchanged',
    noDifferences: 'No differences detected or service did not return difference fields.'
  },

  uploadTip: 'Recommended size: 800x450px, supports jpg/png format, size not exceeding 2MB'
}


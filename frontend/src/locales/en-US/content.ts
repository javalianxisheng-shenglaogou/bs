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
    seoDescription: 'SEO Description'
  },
  
  status: {
    draft: 'Draft',
    pending: 'Pending',
    approved: 'Approved',
    rejected: 'Rejected',
    published: 'Published',
    archived: 'Archived'
  },
  
  placeholder: {
    title: 'Enter title',
    slug: 'Enter slug',
    content: 'Enter content',
    excerpt: 'Enter excerpt',
    searchKeyword: 'Search title or content',
    selectCategory: 'Select category',
    selectStatus: 'Select status',
    selectTags: 'Select tags'
  },
  
  validation: {
    titleRequired: 'Title is required',
    titleLength: 'Title must be 1-200 characters',
    contentRequired: 'Content is required',
    categoryRequired: 'Category is required'
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
    cannotEditPublished: 'Published content cannot be edited directly, please unpublish first'
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
  }
}


{
  "targets": [
    { 
      'target_name': 'simple',
      'sources': [ 
          './main.ts',
        ],
        'include_dirs': [
          '<!(node -e "require(\'nan\')")',
      ],
    }
  ]
}

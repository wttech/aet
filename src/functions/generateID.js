const generateID = block => {
  return (block.type.toString().toLowerCase() + "-" + block.group.toString().toLowerCase()).replace(/\s/g, '');
}

export default generateID;